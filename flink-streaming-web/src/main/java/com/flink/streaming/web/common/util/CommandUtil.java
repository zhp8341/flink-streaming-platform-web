package com.flink.streaming.web.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunYarnDTO;
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
    private final  static  String JARVERSION="lib/flink-streaming-core_flink_1.10.0-1.0.0.RELEASE.jar";

    /**
     * 本地模式
     *
     * @author zhuhuipei
     * @date 2020/11/1
     * @time 09:59
     */
    public static String buildRunCommandForLocal(JobRunYarnDTO jobRunYarnDTO, JobConfigDTO jobConfig, StringBuilder localLog) throws ParseException {
        StringBuilder command = new StringBuilder();
        command.append(jobRunYarnDTO.getFlinkBinPath()).append(" run -d ");
        appendUdfJarPath(command, jobConfig, jobRunYarnDTO, localLog, DeployModeEnum.LOCAL);
        command.append("-c  com.flink.streaming.core.JobApplication").append(" ");
        command.append(jobRunYarnDTO.getSysHome()).append(JARVERSION);
        command.append(" -sql ").append(jobRunYarnDTO.getSqlPath()).append(" ");
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
    public static String buildRunCommandForYarnCluster(JobRunYarnDTO jobRunYarnDTO, JobConfigDTO jobConfig, StringBuilder localLog, String savepointPath) throws ParseException {
        StringBuilder command = new StringBuilder();
        command.append(jobRunYarnDTO.getFlinkBinPath()).append(" run ");
        if (StringUtils.isNotEmpty(savepointPath)) {
            command.append(" -s ").append(savepointPath).append(" ");
        }
        command.append(jobRunYarnDTO.getFlinkRunParam()).append(" ");
        command.append(" -ynm ").append(JobConfigDTO.buildRunName(jobConfig.getJobName())).append(" ");
        command.append(" -yd -m yarn-cluster ").append(" ");
        appendUdfJarPath(command, jobConfig, jobRunYarnDTO, localLog, DeployModeEnum.YARN_PER);
        command.append("-c  com.flink.streaming.core.JobApplication").append(" ");
        command.append(jobRunYarnDTO.getSysHome()).append(JARVERSION);
        command.append(" -sql ").append(jobRunYarnDTO.getSqlPath()).append(" ");
        if (StringUtils.isNotEmpty(jobRunYarnDTO.getFlinkCheckpointConfig())) {
            command.append(" ").append(jobRunYarnDTO.getFlinkCheckpointConfig());
        }
        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath())) {
            command.append(" -udfJarPath ").append(jobConfig.getUdfJarPath());
        }
        return command.toString();
    }


    private static void appendUdfJarPath(StringBuilder command, JobConfigDTO jobConfig, JobRunYarnDTO jobRunYarnDTO, StringBuilder localLog, DeployModeEnum deployModeEnum) {

        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath())) {
            String fileName = System.currentTimeMillis() + "_udf.jar";
            String udfJarPath = jobRunYarnDTO.getSysHome() + "tmp/udf_jar/" + DateUtil.formatDate(new Date()) + "/" + UUID.fastUUID();
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
            if (DeployModeEnum.LOCAL.name().equals(deployModeEnum.name())) {
                command.append("-C ").append("file://"+udfJarPath+"/"+fileName).append(" ");
            }
        }
    }
}
