package com.flink.streaming.core.checkpoint;


import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.CheckPointParameterEnums;
import com.flink.streaming.common.enums.StateBackendEnum;
import com.flink.streaming.common.model.CheckPointParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 19:56
 */
@Slf4j
public class CheckPointParams {

  /**
   * 构建checkPoint参数
   *
   * @author zhuhuipei
   * @date 2020-08-23
   * @time 22:44
   */
  public static CheckPointParam buildCheckPointParam(ParameterTool parameterTool) throws Exception {

    String checkpointDir = parameterTool
        .get(CheckPointParameterEnums.checkpointDir.name(), SystemConstant.SPACE);
    //如果checkpointDir为空不启用CheckPoint
    if (StringUtils.isEmpty(checkpointDir)) {
      return null;
    }
    String checkpointingMode = parameterTool.get(CheckPointParameterEnums.checkpointingMode.name(),
        CheckpointingMode.EXACTLY_ONCE.name());

    String checkpointInterval = parameterTool
        .get(CheckPointParameterEnums.checkpointInterval.name(),
            SystemConstant.SPACE);

    String checkpointTimeout = parameterTool
        .get(CheckPointParameterEnums.checkpointTimeout.name(), SystemConstant.SPACE);

    String tolerableCheckpointFailureNumber =
        parameterTool.get(CheckPointParameterEnums.tolerableCheckpointFailureNumber.name(),
            SystemConstant.SPACE);

    String asynchronousSnapshots = parameterTool
        .get(CheckPointParameterEnums.asynchronousSnapshots.name(), SystemConstant.SPACE);

    String externalizedCheckpointCleanup =
        parameterTool.get(CheckPointParameterEnums.externalizedCheckpointCleanup.name(),
            SystemConstant.SPACE);

    String stateBackendType = parameterTool
        .get(CheckPointParameterEnums.stateBackendType.name(), SystemConstant.SPACE);

    String enableIncremental = parameterTool
        .get(CheckPointParameterEnums.enableIncremental.name(), SystemConstant.SPACE);

    CheckPointParam checkPointParam = new CheckPointParam();
    if (StringUtils.isNotEmpty(asynchronousSnapshots)) {
      checkPointParam.setAsynchronousSnapshots(Boolean.parseBoolean(asynchronousSnapshots));
    }
    checkPointParam.setCheckpointDir(checkpointDir);

    checkPointParam.setCheckpointingMode(checkpointingMode);
    if (StringUtils.isNotEmpty(checkpointInterval)) {
      checkPointParam.setCheckpointInterval(Long.valueOf(checkpointInterval));
    }
    if (StringUtils.isNotEmpty(checkpointTimeout)) {
      checkPointParam.setCheckpointTimeout(Long.valueOf(checkpointTimeout));
    }
    if (StringUtils.isNotEmpty(tolerableCheckpointFailureNumber)) {
      checkPointParam
          .setTolerableCheckpointFailureNumber(Integer.valueOf(tolerableCheckpointFailureNumber));
    }
    if (StringUtils.isNotEmpty(externalizedCheckpointCleanup)) {
      checkPointParam.setExternalizedCheckpointCleanup(externalizedCheckpointCleanup);
    }

    checkPointParam.setStateBackendEnum(StateBackendEnum.getStateBackend(stateBackendType));

    if (StringUtils.isNotEmpty(enableIncremental)) {
      checkPointParam.setEnableIncremental(Boolean.parseBoolean(enableIncremental.trim()));
    }
    log.info("checkPointParam={}", checkPointParam);
    System.out.println("checkPointParam=" + checkPointParam);
    return checkPointParam;

  }

}
