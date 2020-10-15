package com.flink.streaming.web.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunYarnDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 00:56
 */
@Slf4j
public class CommandUtil {


    /**
     *下载udf jar并且构建运行命令
     * @author zhuhuipei
     * @date 2020-09-18
     * @time 00:57
     */
    public static String buildRunCommand(JobRunYarnDTO jobRunYarnDTO, JobConfigDTO jobConfig, StringBuilder localLog, String savepointPath) throws ParseException {
        StringBuilder command = new StringBuilder();
        command.append(jobRunYarnDTO.getFlinkBinPath()).append(" run ");
        if (StringUtils.isNotEmpty(savepointPath)){
            command.append(" -s ").append(savepointPath).append(" ");
        }
        command.append(jobRunYarnDTO.getFlinkRunParam()).append(" ");

        command.append(" -ynm ").append(JobConfigDTO.buildRunName(jobConfig.getJobName())).append(" ");
        command.append(" -yd -m yarn-cluster ").append(" ");
        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath())) {
            String udfJarPath = jobRunYarnDTO.getSysHome() + "tmp/udf_jar/" + DateUtil.formatDate(new Date()) + "/" + UUID.fastUUID();
            localLog.append("生成udf文件目录:").append(udfJarPath).append("\n");
            FileUtils.mkdirs(udfJarPath);
            localLog.append("生成udf文件目录完成").append("\n");
            Set<String> urls = AttributeUtils.toSet(jobConfig.getUdfJarPath());
            for (String url : urls) {
                String[] cmds ={"curl","-o",  udfJarPath + "/" + System.currentTimeMillis() + "_udf.jar", url};
                try {
                    localLog.append("下载地址命令:").append(Arrays.toString(cmds)).append("\n");
                    HttpUtil.downFileByCurl(cmds);
                } catch (Exception e) {
                    log.error("下载失败：{}", Arrays.toString(cmds), e);
                    throw new BizException("下载udf文件失败");
                }
            }
            command.append("-yt ").append(udfJarPath).append(" ");
        }
        command.append("-c  com.flink.streaming.core.JobApplication").append(" ");

        //TODO 不能写死
        command.append(jobRunYarnDTO.getSysHome()).append("lib/flink-streaming-core_flink_1.10.0-1.0.0.RELEASE.jar ");
        command.append(" -sql ").append(jobRunYarnDTO.getSqlPath()).append(" ");

        if (StringUtils.isNotEmpty(jobRunYarnDTO.getFlinkCheckpointConfig())) {
            command.append(" ").append(jobRunYarnDTO.getFlinkCheckpointConfig());
        }

        if (StringUtils.isNotEmpty(jobConfig.getUdfJarPath()) && StringUtils.isNotEmpty(jobConfig.getUdfRegisterName())) {
            command.append(" -udfJarPath ").append(jobConfig.getUdfJarPath());
            command.append(" -udfRegisterName ").append(jobConfig.getUdfRegisterName().replaceAll("\n",""));
        }

        return command.toString();
    }
}
