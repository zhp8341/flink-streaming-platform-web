package com.flink.streaming.web.common.util;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunParamDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 00:56
 */
@Slf4j
public class CommandUtil {

    private static final String APP_CLASS_NAME = "com.flink.streaming.core.JobApplication";

    /**
     * 本地/Standalone Cluster模式
     *
     * @author zhuhuipei
     * @date 2020/11/1
     * @time 09:59
     */
    public static String buildRunCommandForCluster(JobRunParamDTO jobRunParamDTO,
                                                   JobConfigDTO jobConfigDTO, String savepointPath) throws Exception {
        StringBuilder command = new StringBuilder();
        command.append(jobRunParamDTO.getFlinkBinPath()).append(" run -d ");

        if (StringUtils.isNotEmpty(savepointPath)) {
            command.append(" -s ").append(savepointPath).append(" ");
        }

        if (jobConfigDTO.getDeployModeEnum() == DeployModeEnum.STANDALONE) {
            command.append(jobConfigDTO.getFlinkRunConfig());
        }

        if (StringUtils.isNotEmpty(jobConfigDTO.getExtJarPath())) {
            String[] urls = jobConfigDTO.getExtJarPath().split(SystemConstant.LINE_FEED);
            for (String url : urls) {
                command.append(" -C ").append(url.trim()).append(" ");
            }
        }
        switch (jobConfigDTO.getJobTypeEnum()) {
            case SQL_BATCH:
            case SQL_STREAMING:
                command.append("-c  ").append(APP_CLASS_NAME).append(" ");
                command.append(jobRunParamDTO.getSysHome()).append(SystemConstant.JARVERSION);
                command.append(" -sql ").append(jobRunParamDTO.getSqlPath()).append(" ");
                if (StringUtils.isNotEmpty(jobRunParamDTO.getFlinkCheckpointConfig())) {
                    command.append(" ").append(jobRunParamDTO.getFlinkCheckpointConfig());
                }
                command.append(" -type ").append(jobConfigDTO.getJobTypeEnum().getCode()).append(" ");
                break;
            case JAR:
                command.append("-c  ").append(jobConfigDTO.getCustomMainClass()).append(" ");
                command.append(jobRunParamDTO.getMainJarPath());
                command.append(" ").append(jobConfigDTO.getCustomArgs());
                break;
        }

        log.info("buildRunCommandForLocal runCommand={}", command.toString());
        return command.toString();
    }

    /**
     * jar并且构建运行命令
     *
     * @author zhuhuipei
     * @date 2020-09-18
     * @time 00:57
     */
    public static String buildRunCommandForYarnCluster(JobRunParamDTO jobRunParamDTO,
                                                       JobConfigDTO jobConfigDTO, String savepointPath) throws Exception {
        StringBuilder command = new StringBuilder();
        command.append(jobRunParamDTO.getFlinkBinPath()).append(" run ");
        if (StringUtils.isNotEmpty(savepointPath)) {
            command.append(" -s ").append(savepointPath).append(" ");
        }
        command.append(jobRunParamDTO.getFlinkRunParam()).append(" ");
        command.append(" -ynm ").append(JobConfigDTO.buildRunName(jobConfigDTO.getJobName())).append(" ");
        command.append(" -yd -m yarn-cluster ").append(" ");

        if (StringUtils.isNotEmpty(jobConfigDTO.getExtJarPath())) {
            String[] urls = jobConfigDTO.getExtJarPath().split(SystemConstant.LINE_FEED);
            for (String url : urls) {
                command.append(" -C ").append(url.trim()).append(" ");
            }
        }

        switch (jobConfigDTO.getJobTypeEnum()) {
            case SQL_STREAMING:
            case SQL_BATCH:
                command.append("-c ").append(APP_CLASS_NAME).append(" ");
                command.append(jobRunParamDTO.getSysHome()).append(SystemConstant.JARVERSION);
                command.append(" -sql ").append(jobRunParamDTO.getSqlPath()).append(" ");
                if (StringUtils.isNotEmpty(jobRunParamDTO.getFlinkCheckpointConfig())) {
                    command.append(" ").append(jobRunParamDTO.getFlinkCheckpointConfig());
                }
                command.append(" -type ").append(jobConfigDTO.getJobTypeEnum().getCode()).append(" ");
                break;
            case JAR:
                command.append("-c  ").append(jobConfigDTO.getCustomMainClass()).append(" ");
                command.append(jobRunParamDTO.getMainJarPath());
                command.append(" ").append(jobConfigDTO.getCustomArgs());
        }

        log.info("buildRunCommandForYarnCluster runCommand={}", command.toString());
        return command.toString();
    }


    public static String buildSavepointCommandForYarn(String jobId, String targetDirectory, String yarnAppId,
                                                      String flinkHome) {
        StringBuilder command = new StringBuilder(
                SystemConstants.buildFlinkBin(flinkHome));
        command.append(" savepoint ")
                .append(jobId).append(" ")
                .append(targetDirectory).append(" ")
                .append("-yid ").append(yarnAppId);
        return command.toString();
    }


    public static String buildSavepointCommandForCluster(String jobId, String targetDirectory,
                                                         String flinkHome) {
        StringBuilder command = new StringBuilder(
                SystemConstants.buildFlinkBin(flinkHome));
        command.append(" savepoint ")
                .append(jobId).append(" ")
                .append(targetDirectory).append(" ");
        return command.toString();
    }


}
