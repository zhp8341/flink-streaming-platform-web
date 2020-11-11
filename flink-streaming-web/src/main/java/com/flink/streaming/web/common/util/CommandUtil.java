package com.flink.streaming.web.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunParamDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 00:56
 */
@Slf4j
public class CommandUtil {


    //TODO 不能写死
    private final  static  String JARVERSION="lib/flink-streaming-core_flink_1.11.1-1.1.0.RELEASE.jar";

    /**
     * 本地模式
     *
     * @author zhuhuipei
     * @date 2020/11/1
     * @time 09:59
     */
    public static String buildRunCommandForLocal(JobRunParamDTO jobRunParamDTO, JobConfigDTO jobConfig) throws ParseException {
        StringBuilder command = new StringBuilder();
        command.append(jobRunParamDTO.getFlinkBinPath()).append(" run -d ");
        if (jobConfig.getDeployModeEnum()==DeployModeEnum.STANDALONE){
            command.append(jobConfig.getFlinkRunConfig());
        }
        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath())){
            command.append(" -C ").append(jobConfig.getUdfJarPath()).append(" ");
        }

        command.append("-c  com.flink.streaming.core.JobApplication").append(" ");
        command.append(jobRunParamDTO.getSysHome()).append(JARVERSION);
        command.append(" -sql ").append(jobRunParamDTO.getSqlPath()).append(" ");
        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath())) {
            command.append(" -udfJarPath ").append(jobConfig.getUdfJarPath());
        }
        log.info("buildRunCommandForLocal runCommand={}", command.toString());
        return command.toString();
    }

    /**
     * 下载udf jar并且构建运行命令
     *
     * @author zhuhuipei
     * @date 2020-09-18
     * @time 00:57
     */
    public static String buildRunCommandForYarnCluster(JobRunParamDTO jobRunParamDTO, JobConfigDTO jobConfig, StringBuilder localLog, String savepointPath) throws ParseException {
        StringBuilder command = new StringBuilder();
        command.append(jobRunParamDTO.getFlinkBinPath()).append(" run ");
        if (StringUtils.isNotEmpty(savepointPath)) {
            command.append(" -s ").append(savepointPath).append(" ");
        }
        command.append(jobRunParamDTO.getFlinkRunParam()).append(" ");
        command.append(" -ynm ").append(JobConfigDTO.buildRunName(jobConfig.getJobName())).append(" ");
        command.append(" -yd -m yarn-cluster ").append(" ");
        appendUdfJarPath(command, jobConfig, jobRunParamDTO, localLog, DeployModeEnum.YARN_PER);
        command.append("-c  com.flink.streaming.core.JobApplication").append(" ");
        command.append(jobRunParamDTO.getSysHome()).append(JARVERSION);
        command.append(" -sql ").append(jobRunParamDTO.getSqlPath()).append(" ");
        if (StringUtils.isNotEmpty(jobRunParamDTO.getFlinkCheckpointConfig())) {
            command.append(" ").append(jobRunParamDTO.getFlinkCheckpointConfig());
        }
        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath())) {
            command.append(" -udfJarPath ").append(jobConfig.getUdfJarPath());
        }
        return command.toString();
    }


    private static void appendUdfJarPath(StringBuilder command, JobConfigDTO jobConfig, JobRunParamDTO jobRunParamDTO, StringBuilder localLog, DeployModeEnum deployModeEnum) {

        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath())) {
            String fileName = System.currentTimeMillis() + "_udf.jar";
            String udfJarPath = jobRunParamDTO.getSysHome() + "tmp/udf_jar/" + DateUtil.formatDate(new Date()) + "/" + UUID.fastUUID();
            localLog.append("生成udf文件目录:").append(udfJarPath).append("\n");
            FileUtils.mkdirs(udfJarPath);
            localLog.append("生成udf文件目录完成").append("\n");

            String[] cmds = {"curl", "-o", udfJarPath + "/" + fileName, jobConfig.getUdfJarPath()};
            try {
                localLog.append("下载地址命令:").append(Arrays.toString(cmds)).append("\n");
                HttpUtil.downFileByCurl(cmds);
            } catch (Exception e) {
                log.error("下载失败：{}", Arrays.toString(cmds), e);
                throw new BizException("下载udf文件失败");
            }
            if (DeployModeEnum.YARN_PER.name().equals(deployModeEnum.name())) {
                command.append("-yt ").append(udfJarPath).append(" ");
            }

        }
    }
}
