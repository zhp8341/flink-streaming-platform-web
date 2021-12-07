package com.flink.streaming.web.ao.impl;

import cn.hutool.core.date.DateUtil;
import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.JobTypeEnum;
import com.flink.streaming.web.ao.JobBaseServiceAO;
import com.flink.streaming.web.common.MessageConstants;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.TipsConstants;
import com.flink.streaming.web.common.util.*;
import com.flink.streaming.web.config.JobThreadPool;
import com.flink.streaming.web.enums.*;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.model.dto.JobRunParamDTO;
import com.flink.streaming.web.model.dto.SystemConfigDTO;
import com.flink.streaming.web.rpc.CommandRpcClinetAdapter;
import com.flink.streaming.web.rpc.FlinkRestRpcAdapter;
import com.flink.streaming.web.rpc.YarnRestRpcAdapter;
import com.flink.streaming.web.rpc.model.JobStandaloneInfo;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.JobRunLogService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/28
 * @time 10:02
 */
@Component
@Slf4j
public class JobBaseServiceAOImpl implements JobBaseServiceAO {


    @Autowired
    private JobRunLogService jobRunLogService;


    @Autowired
    private YarnRestRpcAdapter yarnRestRpcAdapter;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private CommandRpcClinetAdapter commandRpcClinetAdapter;


    @Autowired
    private JobConfigService jobConfigService;

    @Autowired
    private FlinkRestRpcAdapter flinkRestRpcAdapter;

    @Override
    public void checkStart(JobConfigDTO jobConfigDTO) {
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
        switch (jobConfigDTO.getDeployModeEnum()) {
            case YARN_PER:
                this.checYarnQueue(jobConfigDTO);
                RestResult restResult = CliConfigUtil.checkFlinkRunConfigForYarn(jobConfigDTO.getFlinkRunConfig());
                if (restResult != null && !restResult.isSuccess()) {
                    throw new BizException("启动参数校验没有通过：" + restResult.getMessage());
                }
                break;
        }

        Map<String, String> systemConfigMap = SystemConfigDTO.toMap(systemConfigService.getSystemConfig(SysConfigEnumType.SYS));
        this.checkSysConfig(systemConfigMap, jobConfigDTO.getDeployModeEnum());


    }

    @Override
    public void checkSavepoint(JobConfigDTO jobConfigDTO) {
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
//        if (JobTypeEnum.JAR.equals(jobConfigDTO.getJobTypeEnum())) {
//            log.warn(MessageConstants.MESSAGE_006, jobConfigDTO.getJobName());
//            throw new BizException(MessageConstants.MESSAGE_006);
//        }
        if (JobTypeEnum.SQL_BATCH.equals(jobConfigDTO.getJobTypeEnum())) {
            log.warn(MessageConstants.MESSAGE_010, jobConfigDTO.getJobName());
            throw new BizException(MessageConstants.MESSAGE_010);
        }

        if (StringUtils.isEmpty(jobConfigDTO.getFlinkCheckpointConfig()) &&
                DeployModeEnum.STANDALONE.equals(jobConfigDTO.getDeployModeEnum())) {
            log.error(MessageConstants.MESSAGE_004, jobConfigDTO);
            throw new BizException(MessageConstants.MESSAGE_004);
        }
        if (StringUtils.isEmpty(jobConfigDTO.getJobId())) {
            log.warn(MessageConstants.MESSAGE_005, jobConfigDTO.getJobName());
            throw new BizException(MessageConstants.MESSAGE_005);
        }


    }

    @Override
    public void checkClose(JobConfigDTO jobConfigDTO) {

        if (jobConfigDTO.getStatus().equals(JobConfigStatus.RUN)) {
            throw new BizException(MessageConstants.MESSAGE_002);
        }
        if (jobConfigDTO.getStatus().equals(JobConfigStatus.STARTING)) {
            throw new BizException(MessageConstants.MESSAGE_003);
        }
    }

    @Override
    public Long insertJobRunLog(JobConfigDTO jobConfigDTO, String userName) {
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
        jobRunLogDTO.setRunIp(IpUtil.getInstance().getLocalIP());
        return jobRunLogService.insertJobRunLog(jobRunLogDTO);
    }

    @Override
    public JobRunParamDTO writeSqlToFile(JobConfigDTO jobConfigDTO) {

        Map<String, String> systemConfigMap = SystemConfigDTO.toMap(systemConfigService.getSystemConfig(SysConfigEnumType.SYS));

        String sqlPath = FileUtils.getSqlHome(systemConfigMap.get(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey()))
                + FileUtils.createFileName(String.valueOf(jobConfigDTO.getId()));
        FileUtils.writeText(sqlPath, jobConfigDTO.getFlinkSql(), Boolean.FALSE);

        return JobRunParamDTO.buildJobRunParam(systemConfigMap, jobConfigDTO, sqlPath);
    }

    @Override
    public void aSyncExecJob(JobRunParamDTO jobRunParamDTO, JobConfigDTO jobConfigDTO,
                             Long jobRunLogId, String savepointPath) {
        ThreadPoolExecutor threadPoolExecutor = JobThreadPool.getInstance().getThreadPoolExecutor();
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String jobStatus = JobStatusEnum.SUCCESS.name();
                String appId = "";
                boolean success = true;
                StringBuilder localLog = new StringBuilder()
                        .append("开始提交任务：")
                        .append(DateUtil.now()).append(SystemConstant.LINE_FEED)
                        .append("三方jar: ").append(SystemConstant.LINE_FEED)
                        .append(jobConfigDTO.getExtJarPath())
                        .append(SystemConstant.LINE_FEED)
                        .append("客户端IP：").append(IpUtil.getInstance().getLocalIP())
                        .append(SystemConstant.LINE_FEED);

                try {
                    String command = "";

                    //如果是自定义提交jar模式下载文件到本地
                    this.downJar(jobRunParamDTO, jobConfigDTO);


                    switch (jobConfigDTO.getDeployModeEnum()) {
                        case YARN_PER:
                            //1、构建执行命令
                            command = CommandUtil.buildRunCommandForYarnCluster(jobRunParamDTO,
                                    jobConfigDTO, savepointPath);
                            //2、提交任务
                            appId = this.submitJobForYarn(command, jobConfigDTO, localLog);
                            break;
                        case LOCAL:
                        case STANDALONE:
                            //1、构建执行命令
                            command = CommandUtil.buildRunCommandForCluster(jobRunParamDTO, jobConfigDTO, savepointPath);
                            //2、提交任务
                            appId = this.submitJobForStandalone(command, jobConfigDTO, localLog);

                            break;
                    }


                } catch (Exception e) {
                    log.error("exe is error", e);
                    localLog.append(e).append(errorInfoDir());
                    success = false;
                    jobStatus = JobStatusEnum.FAIL.name();
                } finally {
                    localLog.append("\n启动结束时间: ").append(DateUtil.now()).append(SystemConstant.LINE_FEED);
                    if (success) {
                        localLog.append("######启动结果是 成功############################## ");
                    } else {
                        localLog.append("######启动结果是 失败############################## ");
                    }
                    this.updateStatusAndLog(jobConfigDTO, jobRunLogId, jobStatus, localLog.toString(), appId);
                }

            }


            /**
             *下载文件到本地并且setMainJarPath
             * @author zhuhuipei
             * @date 2021/3/28
             * @time 14:58
             */
            private void downJar(JobRunParamDTO jobRunParamDTO, JobConfigDTO jobConfigDTO) {
                if (JobTypeEnum.JAR.getCode() == jobConfigDTO.getJobTypeEnum().getCode()) {
                    String savePath = jobRunParamDTO.getSysHome() + "tmp/udf_jar/";
                    try {
                        String pathName = UrlUtil.downLoadFromUrl(jobConfigDTO.getCustomJarUrl(), savePath);
                        jobRunParamDTO.setMainJarPath(pathName);
                    } catch (Exception e) {
                        log.error("文件下载失败 {}", jobConfigDTO.getCustomJarUrl(), e);
                        throw new BizException("文件下载失败");
                    }
                }
            }

            /**
             *错误日志目录提示
             * @author zhuhuipei
             * @date 2020-10-19
             * @time 21:47
             */
            private String errorInfoDir() {
                StringBuilder errorTips = new StringBuilder(SystemConstant.LINE_FEED)
                        .append(SystemConstant.LINE_FEED)
                        .append("（重要）请登陆服务器分别查看下面两个目录下的错误日志")
                        .append(IpUtil.getInstance().getLocalIP()).append(SystemConstant.LINE_FEED)
                        .append("web系统日志目录（web日志）：")
                        .append(systemConfigService
                                .getSystemConfigByKey(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey()))
                        .append("logs/error.log").append(SystemConstant.LINE_FEED)
                        .append("flink提交日志目录（flink客户端日志：")
                        .append(systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey()))
                        .append("log/)").append(SystemConstant.LINE_FEED)
                        .append(SystemConstant.LINE_FEED)
                        .append(SystemConstant.LINE_FEED);
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

                        //批任务提交完成后算成功
                        if (JobTypeEnum.SQL_BATCH.equals(jobConfig.getJobTypeEnum())) {
                            jobConfigDTO.setStatus(JobConfigStatus.SUCCESS);
                        } else {
                            jobConfigDTO.setStatus(JobConfigStatus.RUN);
                        }

                        jobConfigDTO.setLastStartTime(new Date());
                        jobConfigDTO.setJobId(appId);
                        jobRunLogDTO.setJobId(appId);
                        if (DeployModeEnum.LOCAL.equals(jobConfig.getDeployModeEnum()) ||
                                DeployModeEnum.STANDALONE.equals(jobConfig.getDeployModeEnum())) {
                            jobRunLogDTO.setRemoteLogUrl(systemConfigService.getFlinkHttpAddress(jobConfig.getDeployModeEnum())
                                    + SystemConstants.HTTP_STANDALONE_APPS + jobConfigDTO.getJobId());
                        }
                        if (DeployModeEnum.YARN_PER.equals(jobConfig.getDeployModeEnum())) {
                            jobRunLogDTO.setRemoteLogUrl(systemConfigService.getYarnRmHttpAddress()
                                    + SystemConstants.HTTP_YARN_CLUSTER_APPS + jobConfigDTO.getJobId());
                        }

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

            private String submitJobForStandalone(String command, JobConfigDTO jobConfig, StringBuilder localLog)
                    throws Exception {

                String appId = commandRpcClinetAdapter.submitJob(command, localLog, jobRunLogId, jobConfig.getDeployModeEnum());
                JobStandaloneInfo jobStandaloneInfo = flinkRestRpcAdapter.getJobInfoForStandaloneByAppId(appId,
                        jobConfig.getDeployModeEnum());

                if (jobStandaloneInfo == null || StringUtils.isNotEmpty(jobStandaloneInfo.getErrors())) {
                    log.error("[submitJobForStandalone] is error jobStandaloneInfo={}", jobStandaloneInfo);
                    localLog.append("\n 任务失败 appId=" + appId);
                    throw new BizException("任务失败");
                } else {
                    if (!SystemConstants.STATUS_RUNNING.equals(jobStandaloneInfo.getState())) {
                        localLog.append("\n 任务失败 appId=" + appId).append("状态是：" + jobStandaloneInfo.getState());
                        throw new BizException("[submitJobForStandalone]任务失败");
                    }
                }
                return appId;
            }

            private String submitJobForYarn(String command, JobConfigDTO jobConfigDTO, StringBuilder localLog)
                    throws Exception {
                commandRpcClinetAdapter.submitJob(command, localLog, jobRunLogId, jobConfigDTO.getDeployModeEnum());
                return yarnRestRpcAdapter.getAppIdByYarn(jobConfigDTO.getJobName(),
                        YarnUtil.getQueueName(jobConfigDTO.getFlinkRunConfig()));
            }

        });
    }


    private void checYarnQueue(JobConfigDTO jobConfigDTO) {
        try {
            String queueName = YarnUtil.getQueueName(jobConfigDTO.getFlinkRunConfig());
            if (StringUtils.isEmpty(queueName)) {
                throw new BizException("无法获取队列名称，请检查你的 flink运行配置参数");
            }
            String appId = yarnRestRpcAdapter.getAppIdByYarn(jobConfigDTO.getJobName(), queueName);
            if (StringUtils.isNotEmpty(appId)) {
                throw new BizException("该任务在yarn上有运行，请到集群上取消任务后再运行 任务名称是:" +
                        jobConfigDTO.getJobName() + " 队列名称是:" + queueName + TipsConstants.TIPS_1);
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
    }


    private void checkSysConfig(Map<String, String> systemConfigMap, DeployModeEnum deployModeEnum) {
        if (systemConfigMap == null) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL);
        }
        if (!systemConfigMap.containsKey(SysConfigEnum.FLINK_HOME.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_HOME);
        }

        if (DeployModeEnum.YARN_PER.equals(deployModeEnum)) {
            if (!systemConfigMap.containsKey(SysConfigEnum.YARN_RM_HTTP_ADDRESS.getKey())) {
                throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_YARN_RM_HTTP_ADDRESS);
            }
        }
        if (!systemConfigMap.containsKey(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey())) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_STREAMING_PLATFORM_WEB_HOME);
        }
    }
}
