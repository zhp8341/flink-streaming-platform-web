package com.flink.streaming.web.enums;

import lombok.Getter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 21:45
 */
@Getter
public enum AlartLogTypeEnum {

    DINGDING(1,"钉钉");

    private int code;

    private String desc;

    AlartLogTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AlartLogTypeEnum getAlartLogTypeEnum(Integer code) {
        if (code == null) {
            return null;
        }

        for (AlartLogTypeEnum alartLogTypeEnum : AlartLogTypeEnum.values()) {
            if (alartLogTypeEnum.getCode() == code.intValue()) {
                return alartLogTypeEnum;
            }

        }
        return null;
    }


}
