package com.flink.streaming.web.ao.impl;

import cn.hutool.core.date.DateUtil;
import com.flink.streaming.web.adapter.CommandAdapter;
import com.flink.streaming.web.adapter.FlinkHttpRequestAdapter;
import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.common.MessageConstants;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.config.JobThreadPool;
import com.flink.streaming.web.common.util.CommandUtil;
import com.flink.streaming.web.common.util.FileUtils;
import com.flink.streaming.web.common.util.IpUtil;
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
import com.flink.streaming.web.model.flink.JobStandaloneInfo;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.JobRunLogService;
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
@Component("jobStandaloneServerAO")
@Slf4j
public class JobStandaloneServerAOImpl implements JobServerAO {


    @Autowired
    private JobConfigService jobConfigService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private JobRunLogService jobRunLogService;

    @Autowired
    private CommandAdapter commandAdapter;

    @Autowired
    private FlinkHttpRequestAdapter flinkHttpRequestAdapter;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id, Long savepointId, String userName) {

        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
        if (JobConfigStatus.RUN.getCode().equals(jobConfigDTO.getStatus().getCode())) {
            throw new BizException("任务运行中请先停止任务");
        }
        if (jobConfigDTO.getStatus().equals(JobConfigStatus.STARTING)) {
            throw new BizException("任务正在启动中 请稍等..");
        }
        if (jobConfigDTO.getIsOpen().intValue() == YN.N.getValue()) {
            throw new BizException("请先开启任务");
        }

        Map<String, String> systemConfigMap = SystemConfigDTO.toMap(systemConfigService.getSystemConfig(SysConfigEnumType.SYS));
        this.checkSysConfig(systemConfigMap, jobConfigDTO.getDeployModeEnum());


        //生产文件并且将sql写入次文件
        String sqlPath = FileUtils.getSqlHome(systemConfigMap.get(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey())) + FileUtils.createFileName(String.valueOf(id));
        FileUtils.writeText(sqlPath, jobConfigDTO.getFlinkSql(), Boolean.FALSE);

        JobRunParamDTO jobRunParamDTO = JobRunParamDTO.getJobRunYarnDTO(systemConfigMap, jobConfigDTO, sqlPath);

        //插入日志表数据
        JobRunLogDTO jobRunLogDTO = new JobRunLogDTO();
        jobRunLogDTO.setDeployMode(jobConfigDTO.getDeployModeEnum().name());
        jobRunLogDTO.setLocalLog(MessageConstants.MESSAGE_001);
        jobRunLogDTO.setJobConfigId(jobConfigDTO.getId());
        jobRunLogDTO.setStartTime(new Date());
        jobRunLogDTO.setJobName(jobConfigDTO.getJobName());
        jobRunLogDTO.setJobId(jobConfigDTO.getJobId());
        jobRunLogDTO.setJobStatus(JobStatusEnum.STARTING.name());
        jobRunLogDTO.setCreator(userName);
        jobRunLogDTO.setEditor(userName);
        Long jobRunLogId = jobRunLogService.insertJobRunLog(jobRunLogDTO);


        //变更任务状态 有乐观锁 防止重复提交
        jobConfigService.updateStatusByStart(jobConfigDTO.getId(), userName, jobRunLogId, jobConfigDTO.getVersion());

        this.aSyncExec(jobRunParamDTO, jobConfigDTO, jobRunLogId);

    }


    @Override
    public void stop(Long id, String userName) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
        JobStandaloneInfo jobStandaloneInfo = flinkHttpRequestAdapter.getJobInfoForStandaloneByAppId(jobConfigDTO.getJobId(), jobConfigDTO.getDeployModeEnum());
        if (jobStandaloneInfo == null || StringUtils.isNotEmpty(jobStandaloneInfo.getErrors())) {
            log.warn("getJobInfoForStandaloneByAppId is error jobStandaloneInfo={}", jobStandaloneInfo);
        } else {
            //停止任务
            if (SystemConstants.STATUS_RUNNING.equals(jobStandaloneInfo.getState())) {
                flinkHttpRequestAdapter.cancelJobForFlinkByAppId(jobConfigDTO.getJobId(), jobConfigDTO.getDeployModeEnum());
            }
        }
        JobConfigDTO jobConfig = new JobConfigDTO();
        jobConfig.setStatus(JobConfigStatus.STOP);
        jobConfig.setEditor(userName);
        jobConfig.setId(id);
        jobConfig.setJobId("");
        //变更状态
        jobConfigService.updateJobConfigById(jobConfig);

    }

    @Override
    public void savepoint(Long id) {
        throw new RuntimeException("Local模式不支持 savepoint");
    }


    @Override
    public void open(Long id, String userName) {
        jobConfigService.openOrClose(id, YN.Y, userName);
    }

    @Override
    public void close(Long id, String userName) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO.getStatus().equals(JobConfigStatus.RUN)) {
            throw new BizException(MessageConstants.MESSAGE_002);
        }
        if (jobConfigDTO.getStatus().equals(JobConfigStatus.STARTING)) {
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
    private void aSyncExec(final JobRunParamDTO jobRunParamDTO, final JobConfigDTO jobConfig, final Long jobRunLogId) {


        ThreadPoolExecutor threadPoolExecutor = JobThreadPool.getInstance().getThreadPoolExecutor();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                String jobStatus = JobStatusEnum.SUCCESS.name();
                String appId = "";
                StringBuilder localLog = new StringBuilder()
                        .append("开始提交任务：")
                        .append(DateUtil.now()).append("\n")
                        .append("客户端IP：").append(IpUtil.getInstance().getLocalIP()).append("\n")
                        .append("运行模式:").append(jobConfig.getDeployModeEnum().name())
                        .append("三方jar:").append(jobConfig.getExtJarPath())
                        .append("\n");

                try {

                    String command = CommandUtil.buildRunCommandForCluster(jobRunParamDTO, jobConfig);
                    appId = commandAdapter.startForLocal(command, localLog, jobRunLogId);
                    Thread.sleep(1000 * 10);
                    JobStandaloneInfo jobStandaloneInfo = flinkHttpRequestAdapter.getJobInfoForStandaloneByAppId(appId, jobConfig.getDeployModeEnum());
                    if (jobStandaloneInfo == null || StringUtils.isNotEmpty(jobStandaloneInfo.getErrors())) {
                        log.error("getJobInfoForStandaloneByAppId is error jobStandaloneInfo={}", jobStandaloneInfo);
                        localLog.append("\n 任务失败 appId=" + appId);
                        throw new BizException("任务失败");
                    } else {
                        if (!SystemConstants.STATUS_RUNNING.equals(jobStandaloneInfo.getState())) {
                            localLog.append("\n 任务失败 appId=" + appId).append("状态是：" + jobStandaloneInfo.getState());
                            throw new BizException("任务失败");
                        }
                    }

                } catch (Exception e) {
                    log.error("exe is error", e);
                    localLog.append(e).append(errorInfoDir());
                    success = false;
                    jobStatus = JobStatusEnum.FAIL.name();
                } finally {
                    localLog.append("\n 启动结束时间 ").append(DateUtil.now()).append("\n\n");
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
                        .append(systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey()))
                        .append("logs/error.log")
                        .append("\n")
                        .append("flink提交日志目录：")
                        .append(systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey()))
                        .append("log/")
                        .append("\n")
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
            private void updateStatusAndLog(JobConfigDTO jobConfig, Long jobRunLogId, String jobStatus,
                                            String localLog, String appId) {
                try {
                    JobConfigDTO jobConfigDTO = new JobConfigDTO();
                    jobConfigDTO.setId(jobConfig.getId());

                    JobRunLogDTO jobRunLogDTO = new JobRunLogDTO();
                    jobRunLogDTO.setId(jobRunLogId);

                    if (JobStatusEnum.SUCCESS.name().equals(jobStatus) && !StringUtils.isEmpty(appId)) {
                        jobConfigDTO.setStatus(JobConfigStatus.RUN);
                        jobConfigDTO.setLastStartTime(new Date());
                        jobConfigDTO.setJobId(appId);
                        jobRunLogDTO.setJobId(appId);
                        jobRunLogDTO.setRemoteLogUrl(systemConfigService.getFlinkHttpAddress(jobConfig.getDeployModeEnum())
                                + SystemConstants.HTTP_STANDALONE_APPS + jobConfigDTO.getJobId());
                    } else {
                        jobConfigDTO.setStatus(JobConfigStatus.FAIL);
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


    private void checkSysConfig(Map<String, String> systemConfigMap, DeployModeEnum deployModeEnum) {
        if (systemConfigMap == null) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL);
        }
        if (!systemConfigMap.containsKey(SysConfigEnum.FLINK_HOME.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_HOME);
        }

        if (DeployModeEnum.LOCAL == deployModeEnum
                && !systemConfigMap.containsKey(SysConfigEnum.FLINK_REST_HTTP_ADDRESS.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_REST_HTTP_ADDRESS);
        }

        if (DeployModeEnum.STANDALONE == deployModeEnum
                && !systemConfigMap.containsKey(SysConfigEnum.FLINK_REST_HA_HTTP_ADDRESS.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_REST_HA_HTTP_ADDRESS);
        }
    }
}
