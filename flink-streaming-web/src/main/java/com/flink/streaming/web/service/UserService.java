package com.flink.streaming.web.service;

import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.model.dto.UserDTO;
import com.flink.streaming.web.model.dto.UserSession;

import java.util.List;

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


    /**
     * 新增用户
     *
     * @author zhuhuipei
     * @date 2020/11/11
     * @time 22:59
     */
    void addUser(String userName, String password, String operator);


    /**
     * 修改密码
     *
     * @author zhuhuipei
     * @date 2020/11/11
     * @time 23:00
     */
    void updatePassword(String userName,String oldPassword, String newPassword, String operator);

    /**
     * 开启或者关闭
     *
     * @author zhuhuipei
     * @date 2020/11/11
     * @time 23:22
     */
    void stopOrOpen(String userName, UserStatusEnum userStatusEnum, String operator);


    /**
     * 获取全部账号
     *
     * @author zhuhuipei
     * @date 2020/11/11
     * @time 23:31
     */
    List<UserDTO> queryAll();


}
