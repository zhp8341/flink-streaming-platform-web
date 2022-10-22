package com.flink.streaming.web.common.util;

import com.flink.streaming.common.enums.CheckPointParameterEnums;
import com.flink.streaming.common.enums.StateBackendEnum;
import com.flink.streaming.common.model.CheckPointParam;
import com.flink.streaming.web.common.FlinkConstants;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.exceptions.BizException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-11
 * @time 00:00
 */
@Slf4j
public class CliConfigUtil {



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

      ParameterTool parameterTool = ParameterTool.fromArgs(config);
      if (parameterTool == null || parameterTool.getUnrequestedParameters() == null) {
        throw new BizException(
            "parameterTool or parameterTool.getUnrequestedParameters() is null ");
      }

      CheckPointParameterEnums.isExits(parameterTool.getUnrequestedParameters());

      String checkpointDir = parameterTool.get(CheckPointParameterEnums.checkpointDir.name());

      String checkpointingMode = parameterTool
          .get(CheckPointParameterEnums.checkpointingMode.name(),
              FlinkConstants.EXACTLY_ONCE);
      String tolerableCheckpointFailureNumber = parameterTool
          .get(CheckPointParameterEnums.tolerableCheckpointFailureNumber.name());
      String asynchronousSnapshots = parameterTool
          .get(CheckPointParameterEnums.asynchronousSnapshots.name());
      String checkpointInterval = parameterTool
          .get(CheckPointParameterEnums.checkpointInterval.name());
      String checkpointTimeout = parameterTool
          .get(CheckPointParameterEnums.checkpointTimeout.name());
      String externalizedCheckpointCleanup = parameterTool
          .get(CheckPointParameterEnums.externalizedCheckpointCleanup.name());
      String stateBackendType = parameterTool.get(CheckPointParameterEnums.stateBackendType.name());
      String enableIncremental = parameterTool
          .get(CheckPointParameterEnums.enableIncremental.name());

      CheckPointParam checkPointParam = new CheckPointParam();
      if (StringUtils.isNotEmpty(asynchronousSnapshots)) {
        if (Boolean.FALSE.toString().equals(asynchronousSnapshots.toLowerCase())
            || Boolean.TRUE.toString().equals(asynchronousSnapshots.toLowerCase())) {
          checkPointParam.setAsynchronousSnapshots(Boolean.valueOf(asynchronousSnapshots));
        } else {
          throw new BizException("asynchronousSnapshots 参数必须是 Boolean 类型或者为空 ");
        }
      }
      if (StringUtils.isNotEmpty(checkpointTimeout)) {
        checkPointParam.setCheckpointTimeout(Long.valueOf(checkpointTimeout));
      }

      checkPointParam.setCheckpointingMode(checkpointingMode);
      if (StringUtils.isNotEmpty(checkpointInterval)) {
        checkPointParam.setCheckpointInterval(Long.valueOf(checkpointInterval));
      }

      if (StringUtils.isNotEmpty(tolerableCheckpointFailureNumber)) {
        checkPointParam
            .setTolerableCheckpointFailureNumber(Integer.valueOf(tolerableCheckpointFailureNumber));
      }
      if (StringUtils.isNotEmpty(externalizedCheckpointCleanup)) {
        checkPointParam.setExternalizedCheckpointCleanup(externalizedCheckpointCleanup);
      }

      //内存模式下不需要填写checkpointDir
      if (!StateBackendEnum.MEMORY.getType().equalsIgnoreCase(stateBackendType)
          && StringUtils.isEmpty(checkpointDir)) {
        throw new BizException("checkpointDir不存在或者没有对应的值");
      }
      checkPointParam.setCheckpointDir(checkpointDir);

      checkPointParam.setStateBackendEnum(StateBackendEnum.getStateBackend(stateBackendType));

      if (StringUtils.isNotEmpty(enableIncremental)) {
        if (Boolean.FALSE.toString().equals(enableIncremental.toLowerCase())
            || Boolean.TRUE.toString().equals(enableIncremental.toLowerCase())) {
          checkPointParam.setEnableIncremental(Boolean.getBoolean(enableIncremental.trim()));
        } else {
          throw new BizException("enableIncremental 参数必须是 Boolean 类型或者为空 ");
        }
      }

      log.info("checkPointParam ={}", checkPointParam);

      return checkPointParam;
    } catch (BizException e) {
      log.error("checkFlinkCheckPoint is error", e);
      throw e;
    } catch (Exception e) {
      log.error("checkFlinkCheckPoint is error", e);
      throw new BizException("Checkpoint参数校验不通过:" + e.getMessage());
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

  public static String getYarnQueueName(String flinkRunConfig) {
    String[] configs = trim(flinkRunConfig);
    for (String config : configs) {
      if (config.contains("-Dyarn.application.queue=")) {
        String value = config.split("=")[1];
        if (StringUtils.isEmpty(value)) {
          return "default";
        }
        return value;
      }
    }
    return "default";
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
