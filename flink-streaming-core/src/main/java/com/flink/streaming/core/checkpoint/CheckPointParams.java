package com.flink.streaming.core.checkpoint;


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

        String checkpointDir = parameterTool.get("checkpointDir");
        //如果checkpointDir为空不启用CheckPoint
        if (StringUtils.isEmpty(checkpointDir)) {
            return null;
        }
        String checkpointingMode = parameterTool.get("checkpointingMode",
                CheckpointingMode.EXACTLY_ONCE.name());

        String checkpointInterval = parameterTool.get("checkpointInterval");

        String checkpointTimeout = parameterTool.get("checkpointTimeout");

        String tolerableCheckpointFailureNumber = parameterTool.get("tolerableCheckpointFailureNumber");

        String asynchronousSnapshots = parameterTool.get("asynchronousSnapshots");

        String externalizedCheckpointCleanup = parameterTool.get("externalizedCheckpointCleanup");

        String stateBackendType = parameterTool.get("stateBackendType");

        String enableIncremental = parameterTool.get("enableIncremental");


        CheckPointParam checkPointParam = new CheckPointParam();
        if (StringUtils.isNotEmpty(asynchronousSnapshots)) {
            checkPointParam.setAsynchronousSnapshots(Boolean.getBoolean(asynchronousSnapshots));
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
            checkPointParam.setTolerableCheckpointFailureNumber(Integer.valueOf(tolerableCheckpointFailureNumber));
        }
        if (StringUtils.isNotEmpty(externalizedCheckpointCleanup)) {
            checkPointParam.setExternalizedCheckpointCleanup(externalizedCheckpointCleanup);
        }

        checkPointParam.setStateBackendEnum(StateBackendEnum.getStateBackend(stateBackendType));

        if (StringUtils.isNotEmpty(enableIncremental)) {
            checkPointParam.setEnableIncremental(Boolean.getBoolean(enableIncremental.trim()));
        }
        log.info("checkPointParam={}", checkPointParam);
        System.out.println("checkPointParam=" + checkPointParam);
        return checkPointParam;

    }

}
