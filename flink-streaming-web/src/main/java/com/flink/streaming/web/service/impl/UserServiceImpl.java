package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.Base64Coded;
import com.flink.streaming.web.common.util.Md5Utils;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.mapper.UserMapper;
import com.flink.streaming.web.model.dto.UserSession;
import com.flink.streaming.web.model.entity.User;
import com.flink.streaming.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 21:53
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String login(String userName, String password) {
        User user = userMapper.selectByUsername(userName);
        if (user == null) {
            throw new BizException(SysErrorEnum.USER_IS_NOT_NULL);
        }
        if (!Md5Utils.getMD5String(password).equalsIgnoreCase(user.getPassword())) {
            throw new BizException(SysErrorEnum.USER_PASSWORD_ERROR);
        }
        String userSession = UserSession.toJsonString(user.getUsername(), user.getPassword());

        return Base64Coded.encode(userSession.getBytes());
    }

    @Override
    public boolean checkLogin(UserSession userSession) {
        User user = userMapper.selectByUsername(userSession.getName());
        if (user == null) {
            return false;
        }
        if (!userSession.getPassword().equalsIgnoreCase(Md5Utils.getMD5String(user.getPassword()))) {
            return false;
        }

        return true;
    }
}
