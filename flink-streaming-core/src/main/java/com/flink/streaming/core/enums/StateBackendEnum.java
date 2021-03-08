package com.flink.streaming.core.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/8
 * @time 16:27
 */
@Getter
public enum StateBackendEnum {
    MEMORY("0"), FILE("1"), ROCKSDB("2");

    private String type;

    StateBackendEnum(String type) {
        this.type = type;
    }

    public static StateBackendEnum getStateBackend(String stateBackendType) {
        if (StringUtils.isEmpty(stateBackendType)) {
            return FILE;
        }

        for (StateBackendEnum stateBackendEnum : StateBackendEnum.values()) {
            if (stateBackendEnum.getType().equalsIgnoreCase(stateBackendType.trim())) {
                return stateBackendEnum;
            }

        }
        return FILE;
    }
}
