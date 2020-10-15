package com.flink.streaming.web.enums;

import lombok.Getter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 21:45
 */
@Getter
public enum AlartLogStatusEnum {
    SUCCESS(1, "成功"),
    FAIL(0, "失败"),
    ;

    private int code;


    private String desc;


    AlartLogStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AlartLogStatusEnum getAlartLogStatusEnum(Integer code) {
        if (code == null) {
            return null;
        }

        for (AlartLogStatusEnum alartLogStatusEnum : AlartLogStatusEnum.values()) {
            if (alartLogStatusEnum.getCode() == code.intValue()) {
                return alartLogStatusEnum;
            }

        }
        return null;
    }
}
