package com.flink.streaming.web.common.util;

import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.model.param.CheckPointParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-11
 * @time 00:00
 */
@Slf4j
public class CliConfigUtil {

    /**
     * 检查flink运行启动参数
     *
     * @author zhuhuipei
     * @date 2020-09-11
     * @time 00:04
     */
    public static RestResult checkFlinkRunConfig(String flinkRunConfig) {
        try {
            CommandLine cl = getFlinkRunByCli(flinkRunConfig);
            if (!cl.hasOption(SystemConstants.YQU)) {
                return RestResult.error("任务必须包含队列参数 -yqu ");
            }
        } catch (UnrecognizedOptionException e) {
            log.error("checkFlinkRunConfig is error", e);
            return RestResult.error("flink运行配置参数校验通不过,不允许使用参数：" + e.getOption() + " 参数只支持 -p -yjm -yn -ytm -ys -yqu -yD");
        } catch (Exception e) {
            log.error("checkFlinkRunConfig is error", e);
            return RestResult.error("flink运行配置参数校验通不过");
        }
        return null;
    }


    /**
     * 检查CheckPoint参数
     *
     * @author zhuhuipei
     * @date 2020-09-11
     * @time 00:04
     */
    public static CheckPointParam checkFlinkCheckPoint(String flinkCheckpointConfig) {
        try {
            String[] config = trim(flinkCheckpointConfig);
            Options options = new Options();
            options.addOption("checkpointDir", true, "checkpointDir");
            options.addOption("tolerableCheckpointFailureNumber", true, "tolerableCheckpointFailureNumber");
            options.addOption("asynchronousSnapshots", true, "asynchronousSnapshots");
            options.addOption("checkpointInterval", true, "checkpointInterval");
            options.addOption("checkpointingMode", true, "checkpointingMode");
            options.addOption("checkpointTimeout", true, "checkpointTimeout");
            options.addOption("externalizedCheckpointCleanup", true, "externalizedCheckpointCleanup");

            CommandLineParser parser = new DefaultParser();
            CommandLine cl = parser.parse(options, config);

            String checkpointDir = cl.getOptionValue("checkpointDir");
            //如果checkpointDir为空不启用CheckPoint
            if (StringUtils.isEmpty(checkpointDir)) {
                throw new BizException("checkpointDir参数校验不通过");
            }
            String checkpointingMode = cl.getOptionValue("checkpointingMode", "EXACTLY_ONCE");
            String tolerableCheckpointFailureNumber = cl.getOptionValue("tolerableCheckpointFailureNumber");
            String asynchronousSnapshots = cl.getOptionValue("asynchronousSnapshots");
            String checkpointInterval = cl.getOptionValue("checkpointInterval");
            String checkpointTimeout = cl.getOptionValue("checkpointTimeout");
            String externalizedCheckpointCleanup = cl.getOptionValue("externalizedCheckpointCleanup");

            CheckPointParam checkPointParam = new CheckPointParam();
            if (StringUtils.isNotEmpty(asynchronousSnapshots)) {
                if (Boolean.FALSE.toString().equals(asynchronousSnapshots.toLowerCase()) || Boolean.TRUE.toString().equals(asynchronousSnapshots.toLowerCase())) {
                    checkPointParam.setAsynchronousSnapshots(Boolean.valueOf(asynchronousSnapshots));
                } else {
                    throw new BizException("asynchronousSnapshots 参数必须是 Boolean 类型或者为空 ");
                }

            }
            if (StringUtils.isNotEmpty(checkpointTimeout)) {
                checkPointParam.setCheckpointTimeout(Long.valueOf(checkpointTimeout));
            }
            checkPointParam.setCheckpointDir(checkpointDir);
            checkPointParam.setCheckpointingMode(checkpointingMode);
            if (StringUtils.isNotEmpty(checkpointInterval)) {
                checkPointParam.setCheckpointInterval(Long.valueOf(checkpointInterval));
            }

            if (StringUtils.isNotEmpty(tolerableCheckpointFailureNumber)) {
                checkPointParam.setTolerableCheckpointFailureNumber(Integer.valueOf(tolerableCheckpointFailureNumber));
            }
            if (StringUtils.isNotEmpty(externalizedCheckpointCleanup)) {
                checkPointParam.setExternalizedCheckpointCleanup(externalizedCheckpointCleanup);
            }
            return checkPointParam;
        } catch (UnrecognizedOptionException e) {
            log.error("checkFlinkCheckPoint is error", e);
            throw new BizException("Checkpoint参数校验不通过,不允许使用参数：" + e.getOption());
        } catch (BizException e) {
            log.error("checkFlinkCheckPoint is error", e);
            throw e;
        } catch (Exception e) {
            log.error("checkFlinkCheckPoint is error", e);
            throw new BizException("Checkpoint参数校验不通过");
        }
    }


    public static CommandLine getFlinkRunByCli(String flinkRunConfig) throws ParseException {
        String[] config = trim(flinkRunConfig);
        Options options = new Options();
        options.addOption("p", false, "");
        options.addOption("yjm", false, "");
        options.addOption("yn", false, "");
        options.addOption("ytm", false, "");
        options.addOption("ys", false, "");
        options.addOption("yD", false, "");
        options.addOption(SystemConstants.YQU, true, "");
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, config);
    }


    private static String[] trim(String cliConfig) {

        List<String> list = new ArrayList<>();
        String[] config = cliConfig.split(" ");
        for (String str : config) {
            if (StringUtils.isNotEmpty(str)) {
                list.add(str);
            }
        }
        return list.toArray(new String[list.size()]);
    }

}
