package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.common.util.Base64Coded;
import com.flink.streaming.web.common.util.Md5Utils;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.mapper.UserMapper;
import com.flink.streaming.web.model.dto.UserDTO;
import com.flink.streaming.web.model.dto.UserSession;
import com.flink.streaming.web.model.entity.User;
import com.flink.streaming.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if (UserStatusEnum.CLOSE.getCode().equals(user.getStauts())) {
            throw new BizException(SysErrorEnum.USER_IS_STOP);
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

    @Override
    public void addUser(String userName, String password, String operator) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        User user = userMapper.selectByUsername(userName);
        if (user != null) {
            throw new BizException(SysErrorEnum.USER_IS_EXIST);
        }
        user = new User();
        user.setEditor(operator);
        user.setCreator(operator);
        user.setUsername(userName);
        //默认开启
        user.setStauts(UserStatusEnum.OPEN.getCode());
        user.setPassword(Md5Utils.getMD5String(password));
        userMapper.insert(user);
    }

    @Override
    public void updatePassword(String userName, String oldPassword, String password, String operator) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        User userMp = userMapper.selectByUsername(userName);
        if (userMp == null) {
            throw new BizException(SysErrorEnum.USER_IS_NOT_NULL);
        }
        if (!userMp.getPassword().equals(Md5Utils.getMD5String(oldPassword))) {
            throw new BizException("老的密码不正确");
        }

        User user = new User();
        user.setEditor(operator);
        user.setUsername(userName);
        user.setPassword(Md5Utils.getMD5String(password));
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void stopOrOpen(String userName, UserStatusEnum userStatusEnum, String operator) {
        if (StringUtils.isEmpty(userName) || userStatusEnum == null) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        if (userMapper.selectByUsername(userName) == null) {
            throw new BizException(SysErrorEnum.USER_IS_NOT_NULL);
        }
        User user = new User();
        user.setEditor(operator);
        user.setUsername(userName);
        user.setStauts(userStatusEnum.getCode());
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public List<UserDTO> queryAll() {
        return UserDTO.toListDTO(userMapper.findAll());
    }


}
