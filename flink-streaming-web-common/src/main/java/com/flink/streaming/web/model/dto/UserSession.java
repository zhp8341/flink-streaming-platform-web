package com.flink.streaming.web.model.dto;

import com.alibaba.fastjson.JSON;
import com.flink.streaming.web.common.util.Md5Utils;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 23:20
 */
@Data
public class UserSession {

    private String name;

    private String password;


    public UserSession(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public static String toJsonString(String name, String password) {
        return JSON.toJSONString(new UserSession(name, Md5Utils.getMD5String(password)));
    }


    public static UserSession toUserSession(String json) {
        return JSON.parseObject(json, UserSession.class);
    }
}
