package com.flink.streaming.web.common.util;

import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunParamDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 00:56
 */
@Slf4j
public class CommandUtil {


    //TODO 不能写死
    private final static String JARVERSION = "lib/flink-streaming-core-1.2.0.RELEASE.jar";

    /**
     * 本地/Standalone Cluster模式
     *
     * @author zhuhuipei
     * @date 2020/11/1
     * @time 09:59
     */
    public static String buildRunCommandForCluster(JobRunParamDTO jobRunParamDTO, JobConfigDTO jobConfig) throws ParseException {
        StringBuilder command = new StringBuilder();
        command.append(jobRunParamDTO.getFlinkBinPath()).append(" run -d ");
        if (jobConfig.getDeployModeEnum() == DeployModeEnum.STANDALONE) {
            command.append(jobConfig.getFlinkRunConfig());
        }
        if (StringUtils.isNotEmpty(jobConfig.getExtJarPath())) {
            String[] urls = jobConfig.getExtJarPath().split("\n");
            for (String url : urls) {
                command.append(" -C ").append(url.trim()).append(" ");
            }
        }

        command.append("-c  com.flink.streaming.core.JobApplication").append(" ");
        command.append(jobRunParamDTO.getSysHome()).append(JARVERSION);
        command.append(" -sql ").append(jobRunParamDTO.getSqlPath()).append(" ");
        if (StringUtils.isNotEmpty(jobRunParamDTO.getFlinkCheckpointConfig())) {
            command.append(" ").append(jobRunParamDTO.getFlinkCheckpointConfig());
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
    public static String buildRunCommandForYarnCluster(JobRunParamDTO jobRunParamDTO, JobConfigDTO jobConfig, String savepointPath) throws ParseException {
        StringBuilder command = new StringBuilder();
        command.append(jobRunParamDTO.getFlinkBinPath()).append(" run ");
        if (StringUtils.isNotEmpty(savepointPath)) {
            command.append(" -s ").append(savepointPath).append(" ");
        }
        command.append(jobRunParamDTO.getFlinkRunParam()).append(" ");
        command.append(" -ynm ").append(JobConfigDTO.buildRunName(jobConfig.getJobName())).append(" ");
        command.append(" -yd -m yarn-cluster ").append(" ");

        if (StringUtils.isNotEmpty(jobConfig.getExtJarPath())) {
            String[] urls = jobConfig.getExtJarPath().split("\n");
            for (String url : urls) {
                command.append(" -C ").append(url.trim()).append(" ");
            }
        }


        command.append("-c  com.flink.streaming.core.JobApplication").append(" ");
        command.append(jobRunParamDTO.getSysHome()).append(JARVERSION);
        command.append(" -sql ").append(jobRunParamDTO.getSqlPath()).append(" ");
        if (StringUtils.isNotEmpty(jobRunParamDTO.getFlinkCheckpointConfig())) {
            command.append(" ").append(jobRunParamDTO.getFlinkCheckpointConfig());
        }

        if (StringUtils.isNotEmpty(jobRunParamDTO.getFlinkCatalogType())) {
            command.append(" -catalog ").append(jobRunParamDTO.getFlinkCatalogType());
        }
        if (StringUtils.isNotEmpty(jobRunParamDTO.getHiveCatalogConfDir())) {
            command.append(" -hive_conf_dir ").append(jobRunParamDTO.getHiveCatalogConfDir());
        }

        return command.toString();
    }





}
