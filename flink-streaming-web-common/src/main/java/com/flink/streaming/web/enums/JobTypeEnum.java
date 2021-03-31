package com.flink.streaming.web.enums;

import lombok.Getter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/28
 * @time 11:14
 */
@Getter
public enum JobTypeEnum {

    SQL(0), JAR(1);

    private int code;

    JobTypeEnum(int code) {
        this.code = code;
    }

    public static JobTypeEnum getJobTypeEnum(Integer code) {
        if (code == null) {
            return null;
        }
        for (JobTypeEnum jobTypeEnum : JobTypeEnum.values()) {
            if (code == jobTypeEnum.getCode()) {
                return jobTypeEnum;
            }
        }

        return null;
    }
}
