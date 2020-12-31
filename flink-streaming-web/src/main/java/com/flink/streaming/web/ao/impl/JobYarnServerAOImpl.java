package com.flink.streaming.web.ao.impl;

import cn.hutool.core.date.DateUtil;
import com.flink.streaming.web.adapter.CommandAdapter;
import com.flink.streaming.web.adapter.FlinkHttpRequestAdapter;
import com.flink.streaming.web.adapter.HttpRequestAdapter;
import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.common.MessageConstants;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.thread.AsyncThreadPool;
import com.flink.streaming.web.common.util.CliConfigUtil;
import com.flink.streaming.web.common.util.CommandUtil;
import com.flink.streaming.web.common.util.FileUtils;
import com.flink.streaming.web.common.util.IpUtil;
import com.flink.streaming.web.common.util.YarnUtil;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.JobStatusEnum;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.model.dto.JobRunParamDTO;
import com.flink.streaming.web.model.dto.SystemConfigDTO;
import com.flink.streaming.web.model.flink.JobYarnInfo;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.JobRunLogService;
import com.flink.streaming.web.service.SavepointBackupService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 23:11
 */
@Component("jobYarnServerAO")
@Slf4j
public class JobYarnServerAOImpl implements JobServerAO {

    //最大重试次数
    private static final Integer tryTimes = 2;

    @Autowired
    private JobConfigService jobConfigService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private HttpRequestAdapter httpRequestAdapter;

    @Autowired
    private JobRunLogService jobRunLogService;

    @Autowired
    private CommandAdapter commandAdapter;

    @Autowired
    private FlinkHttpRequestAdapter flinkHttpRequestAdapter;

    @Autowired
    private SavepointBackupService savepointBackupService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id, Long savepointId, String userName) {

        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
        if (JobConfigStatus.RUN.getCode().equals(jobConfigDTO.getStauts().getCode())) {
            throw new BizException("任务运行中请先停止任务");
        }
        if (jobConfigDTO.getStauts().equals(JobConfigStatus.STARTING)) {
            throw new BizException("任务正在启动中 请稍等..");
        }
        if (jobConfigDTO.getIsOpen().intValue() == YN.N.getValue()) {
            throw new BizException("请先开启任务");
        }
        if (StringUtils.isNotEmpty(jobConfigDTO.getJobId())) {
            this.stop(jobConfigDTO);
        }
        try {
            String queueName = YarnUtil.getQueueName(jobConfigDTO.getFlinkRunConfig());
            if (StringUtils.isEmpty(queueName)) {
                throw new BizException("无法获取队列名称，请检查你的 flink运行配置");
            }
            String appId = httpRequestAdapter.getAppIdByYarn(jobConfigDTO.getJobName(), queueName);
            if (StringUtils.isNotEmpty(appId)) {
                throw new BizException("该任务在yarn上有运行，请取消任务后再运行 任务名称是:" + jobConfigDTO.getJobName() + " 队列名称是:" + queueName);
            }
        } catch (BizException e) {
            if (e != null && SysErrorEnum.YARN_CODE.getCode().equals(e.getCode())) {
                log.info(e.getErrorMsg());
            } else {
                throw e;
            }

        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

        RestResult restResult = CliConfigUtil.checkFlinkRunConfig(jobConfigDTO.getFlinkRunConfig());
        if (restResult != null && !restResult.isSuccess()) {
            throw new BizException("启动参数校验没有通过：" + restResult.getMessage());
        }

        Map<String, String> systemConfigMap = SystemConfigDTO.toMap(systemConfigService.getSystemConfig(SysConfigEnumType.SYS));
        this.checkSysConfig(systemConfigMap);


        //生产文件并且将sql写入次文件
        String sqlPath = FileUtils.getSqlHome(systemConfigMap.get(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey()))
                + FileUtils.createFileName(String.valueOf(id));
        FileUtils.writeText(sqlPath, jobConfigDTO.getFlinkSql(), Boolean.FALSE);

        JobRunParamDTO jobRunParamDTO = JobRunParamDTO.getJobRunYarnDTO(systemConfigMap, jobConfigDTO, sqlPath);


        //插入日志表数据
        JobRunLogDTO jobRunLogDTO = new JobRunLogDTO();
        jobRunLogDTO.setDeployMode(DeployModeEnum.YARN_PER.name());
        jobRunLogDTO.setLocalLog(MessageConstants.MESSAGE_001);
        jobRunLogDTO.setJobConfigId(jobConfigDTO.getId());
        jobRunLogDTO.setStartTime(new Date());
        jobRunLogDTO.setJobName(jobConfigDTO.getJobName());
        jobRunLogDTO.setJobId(jobConfigDTO.getJobId());
        jobRunLogDTO.setJobStatus(JobStatusEnum.STARTING.name());
        Long jobRunLogId = jobRunLogService.insertJobRunLog(jobRunLogDTO);

        //变更任务状态
        JobConfigDTO jobConfigUpdate = new JobConfigDTO();
        jobConfigUpdate.setId(jobConfigDTO.getId());
        jobConfigUpdate.setLastRunLogId(jobRunLogId);
        jobConfigUpdate.setStauts(JobConfigStatus.STARTING);
        jobConfigService.updateJobConfigById(jobConfigUpdate);

        String savepointPath = savepointBackupService.getSavepointPathById(id, savepointId);

        this.aSyncExec(jobRunParamDTO, jobConfigDTO, jobRunLogId, savepointPath);

    }


    @Override
    public void stop(Long id, String userName) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }

        //1、停止前做一次savepoint操作
        try {
            this.savepoint(id);
        } catch (Exception e) {
            log.error("autoSavePoint is error");
        }

        //2、停止任务
        this.stop(jobConfigDTO);

        JobConfigDTO jobConfig = new JobConfigDTO();
        jobConfig.setStauts(JobConfigStatus.STOP);
        jobConfig.setEditor(userName);
        jobConfig.setId(id);
        jobConfig.setJobId("");
        //3、变更状态
        jobConfigService.updateJobConfigById(jobConfig);

    }

    @Override
    public void savepoint(Long id) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
        if (StringUtils.isEmpty(jobConfigDTO.getFlinkCheckpointConfig())) {
            log.warn(" FlinkCheckpointConfig is null jobConfigDTO={}", jobConfigDTO);
            return;
        }
        if (StringUtils.isEmpty(jobConfigDTO.getJobId())) {
            log.warn("getJobId is null jobConfigDTO={}", jobConfigDTO);
            return;
        }

        JobYarnInfo jobYarnInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
        if (jobYarnInfo == null) {
            log.warn("jobInfo is null jobConfigDTO={}", jobConfigDTO);
            return;
        }
        //1、 执行savepoint
        try {
            commandAdapter.savepointForPerYarn(jobYarnInfo.getId(),
                    SystemConstants.DEFAULT_SAVEPOINT_ROOT_PATH + id, jobConfigDTO.getJobId());
        } catch (Exception e) {
            log.error("savepointForPerYarn is error", e);
            return;
        }

        String savepointPath = flinkHttpRequestAdapter.getSavepointPath(jobConfigDTO.getJobId(), jobYarnInfo.getId());
        if (StringUtils.isEmpty(savepointPath)) {
            log.warn("getSavepointPath is null jobConfigDTO={}", jobConfigDTO);
        }
        //2、 执行保存Savepoint到本地数据库
        savepointBackupService.insertSavepoint(id, savepointPath, new Date());
    }


    @Override
    public void open(Long id, String userName) {
        jobConfigService.openOrClose(id, YN.Y, userName);
    }

    @Override
    public void close(Long id, String userName) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO.getStauts().equals(JobConfigStatus.RUN)) {
            throw new BizException(MessageConstants.MESSAGE_002);
        }
        if (jobConfigDTO.getStauts().equals(JobConfigStatus.STARTING)) {
            throw new BizException(MessageConstants.MESSAGE_003);
        }
        jobConfigService.openOrClose(id, YN.N, userName);
    }


    /**
     * 异步执行
     *
     * @author zhuhuipei
     * @date 2020-08-07
     * @time 19:18
     */
    private void aSyncExec(final JobRunParamDTO jobRunParamDTO, final JobConfigDTO jobConfig,
                           final Long jobRunLogId, final String savepointPath) {


        ThreadPoolExecutor threadPoolExecutor = AsyncThreadPool.getInstance().getThreadPoolExecutor();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String jobStatus = JobStatusEnum.SUCCESS.name();
                String appId = "";
                boolean success = true;
                StringBuilder localLog = new StringBuilder()
                        .append("开始提交任务：")
                        .append(DateUtil.now()).append("\n")
                        .append("三方jar:").append(jobConfig.getExtJarPath()).append("\n")
                        .append("客户端IP：").append(IpUtil.getInstance().getLocalIP()).append("\n");

                try {
                    String command = CommandUtil.buildRunCommandForYarnCluster(jobRunParamDTO, jobConfig, localLog, savepointPath);
                    commandAdapter.startForPerYarn(command, localLog, jobRunLogId);
                    Thread.sleep(1000 * 10);
                    appId = httpRequestAdapter.getAppIdByYarn(jobConfig.getJobName(), YarnUtil.getQueueName(jobConfig.getFlinkRunConfig()));
                } catch (Exception e) {
                    log.error("exe is error", e);
                    localLog.append(e).append(errorInfoDir());
                    success = false;
                    jobStatus = JobStatusEnum.FAIL.name();
                } finally {
                    localLog.append("\n启动结束时间: ").append(DateUtil.now()).append("\n\n");
                    if (success) {
                        localLog.append("######启动结果是 成功############################## ");
                    } else {
                        localLog.append("######启动结果是 失败############################## ");
                    }
                    this.updateStatusAndLog(jobConfig, jobRunLogId, jobStatus, localLog.toString(), appId);
                }

            }


            /**
             *错误日志目录提示
             * @author zhuhuipei
             * @date 2020-10-19
             * @time 21:47
             */
            private String errorInfoDir() {
                StringBuilder errorTips = new StringBuilder("\n\n")
                        .append("详细错误日志可以登录服务器:")
                        .append(IpUtil.getInstance().getLocalIP()).append("\n")
                        .append("web系统日志目录：")
                        .append(systemConfigService
                                .getSystemConfigByKey(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey()))
                        .append("logs/error.log").append("\n")
                        .append("flink提交日志目录：")
                        .append(systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey()))
                        .append("log/").append("\n")
                        .append("\n")
                        .append("\n");
                return errorTips.toString();
            }


            /**
             * 更新日志、更新配置信息
             * @param jobConfig
             * @param jobRunLogId
             * @param jobStatus
             * @param localLog
             * @param appId
             */
            private void updateStatusAndLog(JobConfigDTO jobConfig, Long jobRunLogId,
                                            String jobStatus, String localLog, String appId) {
                try {
                    JobConfigDTO jobConfigDTO = new JobConfigDTO();
                    jobConfigDTO.setId(jobConfig.getId());
                    JobRunLogDTO jobRunLogDTO = new JobRunLogDTO();
                    jobRunLogDTO.setId(jobRunLogId);
                    if (JobStatusEnum.SUCCESS.name().equals(jobStatus) && !StringUtils.isEmpty(appId)) {
                        jobConfigDTO.setStauts(JobConfigStatus.RUN);
                        jobConfigDTO.setLastStartTime(new Date());
                        jobConfigDTO.setJobId(appId);
                        jobRunLogDTO.setJobId(appId);
                        jobRunLogDTO.setRemoteLogUrl(systemConfigService.getYarnRmHttpAddress()
                                + SystemConstants.HTTP_YARN_CLUSTER_APPS + jobConfigDTO.getJobId());
                    } else {
                        jobConfigDTO.setStauts(JobConfigStatus.FAIL);
                    }
                    jobConfigService.updateJobConfigById(jobConfigDTO);

                    jobRunLogDTO.setJobStatus(jobStatus);
                    jobRunLogDTO.setLocalLog(localLog);
                    jobRunLogService.updateJobRunLogById(jobRunLogDTO);

                    //最后更新一次日志 (更新日志和更新信息分开 防止日志更新失败导致相关状态更新也失败)
                    jobRunLogService.updateLogById(localLog, jobRunLogId);
                } catch (Exception e) {
                    log.error(" localLog.length={} 异步更新数据失败：", localLog.length(), e);
                }
            }
        });
    }


    private void checkSysConfig(Map<String, String> systemConfigMap) {
        if (systemConfigMap == null) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL);
        }
        if (!systemConfigMap.containsKey(SysConfigEnum.FLINK_HOME.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_HOME);
        }
        if (!systemConfigMap.containsKey(SysConfigEnum.YARN_RM_HTTP_ADDRESS.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_YARN_RM_HTTP_ADDRESS);
        }
        if (!systemConfigMap.containsKey(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_STREAMING_PLATFORM_WEB_HOME);
        }
    }


    private void stop(JobConfigDTO jobConfigDTO) {
        Integer retryNum = 1;
        while (retryNum <= tryTimes) {
            JobYarnInfo jobYarnInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
            if (jobYarnInfo != null && SystemConstants.STATUS_RUNNING.equals(jobYarnInfo.getStatus())) {
                log.info("执行停止操作 jobYarnInfo={} retryNum={} id={}", jobYarnInfo, retryNum, jobConfigDTO.getJobId());
                flinkHttpRequestAdapter.cancelJobForYarnByAppId(jobConfigDTO.getJobId(), jobYarnInfo.getId());
            } else {
                log.info("任务已经停止 jobYarnInfo={} id={}", jobYarnInfo, jobConfigDTO.getJobId());
                break;
            }
            retryNum++;
        }
    }
}
