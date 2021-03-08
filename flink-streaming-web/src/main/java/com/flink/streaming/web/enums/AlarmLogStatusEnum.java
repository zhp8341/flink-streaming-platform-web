package com.flink.streaming.web.enums;

import lombok.Getter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 21:45
 */
@Getter
public enum AlarmLogStatusEnum {
    SUCCESS(1, "成功"),
    FAIL(0, "失败"),
    ;

    private int code;


    private String desc;


    AlarmLogStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AlarmLogStatusEnum getAlarmLogStatusEnum(Integer code) {
        if (code == null) {
            return null;
        }

        for (AlarmLogStatusEnum alarmLogStatusEnum : AlarmLogStatusEnum.values()) {
            if (alarmLogStatusEnum.getCode() == code.intValue()) {
                return alarmLogStatusEnum;
            }

        }
        return null;
    }
}
