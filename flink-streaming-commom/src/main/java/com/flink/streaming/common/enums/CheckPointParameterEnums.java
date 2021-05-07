package com.flink.streaming.common.enums;

import java.util.Set;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/21
 * @time 20:01
 */
public enum CheckPointParameterEnums {

    checkpointDir,
    checkpointingMode,
    checkpointInterval,
    checkpointTimeout,
    tolerableCheckpointFailureNumber,
    asynchronousSnapshots,
    externalizedCheckpointCleanup,
    stateBackendType,
    enableIncremental;

    public static void isExits(Set<String> keys) {
        for (String key : keys) {
            boolean exits = false;
            for (CheckPointParameterEnums checkPointParameterEnums : CheckPointParameterEnums.values()) {
                if (checkPointParameterEnums.name().equalsIgnoreCase(key)) {
                    exits = true;
                    continue;
                }
            }
            if (!exits) {
                throw new RuntimeException(key + " 暂时不支持使用");
            }
        }
    }
}
