package com.flink.streaming.common.enums;

import lombok.Getter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/4/19
 * @time 19:15
 */
@Getter
public enum ExecMode {

    BATCH("Batch"), STREAMING("Streaming");

    private final String name;

    ExecMode(String name) {
        this.name = name;
    }

}
