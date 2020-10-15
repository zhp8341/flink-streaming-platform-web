package com.flink.streaming.web.service;

import com.flink.streaming.web.model.dto.UserSession;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 21:52
 */
public interface UserService {

    /**
     * 登陆校验,并且返回sessionId
     *
     * @author zhuhuipei
     * @date 2020-07-13
     * @time 21:54
     */
    String login(String userName, String password);

    /**
     * 登陆帐号Session校验
     *
     * @author zhuhuipei
     * @date 2020-07-13
     * @time 00:11
     */
    boolean checkLogin(UserSession userSession);

}
