package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.common.util.Base64Coded;
import com.flink.streaming.web.common.util.Md5Utils;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.mapper.UserMapper;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.dto.UserDTO;
import com.flink.streaming.web.model.dto.UserSession;
import com.flink.streaming.web.model.entity.User;
import com.flink.streaming.web.model.page.PageParam;
import com.flink.streaming.web.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    if (UserStatusEnum.CLOSE.getCode().equals(user.getStatus())) {
      throw new BizException(SysErrorEnum.USER_IS_STOP);
    }
    if (!Md5Utils.getMD5String(password).equalsIgnoreCase(user.getPassword())) {
      if (password.equals(user.getPassword())) { //数据库保存的非md5码
        String userSession = UserSession.toJsonString(user.getId(), user.getUsername(),
            Md5Utils.getMD5String(user.getPassword()));
        return Base64Coded.encode(userSession.getBytes());
      }
      throw new BizException(SysErrorEnum.USER_PASSWORD_ERROR);
    }
    String userSession = UserSession
        .toJsonString(user.getId(), user.getUsername(), user.getPassword());
    return Base64Coded.encode(userSession.getBytes());
  }

  @Override
  public boolean checkLogin(UserSession userSession) {
    User user = userMapper.selectByUsername(userSession.getName());
    if (user == null) {
      return false;
    }
    if (!userSession.getPassword().equalsIgnoreCase(Md5Utils.getMD5String(user.getPassword()))) {
      if (userSession.getPassword().equalsIgnoreCase(
          Md5Utils.getMD5String(Md5Utils.getMD5String(user.getPassword())))) { //数据库保存的非md5码
        return true;
      }
      return false;
    }

    return true;
  }

  @Override
  public PageModel<UserDTO> queryAllByPage(PageParam pageparam) {
    PageHelper.startPage(pageparam.getPageNum(), pageparam.getPageSize(), YN.Y.getCode());
    Page<User> users = userMapper.queryAllByPage(pageparam);
    PageModel<UserDTO> pageModel = new PageModel<UserDTO>();
    pageModel.setPageNum(users.getPageNum());
    pageModel.setPages(users.getPages());
    pageModel.setPageSize(users.getPageSize());
    pageModel.setTotal(users.getTotal());
    pageModel.addAll(UserDTO.toListDTO(users.getResult()));
    return pageModel;
  }

  @Override
  public void addUser(String userName, String fullname, String password, String operator) {
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
    user.setName(fullname);
    //默认开启
    user.setStatus(UserStatusEnum.OPEN.getCode());
    user.setPassword(Md5Utils.getMD5String(password));
    userMapper.insert(user);
  }

  @Override
  public void updatePassword(String userName, String oldPassword, String password,
      String operator) {
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
  public void updatePassword(Integer userId, String password, String operator) {
    User userMp = userMapper.selectByUserId(userId);
    if (userMp == null) {
      throw new BizException(SysErrorEnum.USER_IS_NOT_NULL);
    }
    User user = new User();
    user.setId(userId);
    user.setEditor(operator);
    user.setPassword(Md5Utils.getMD5String(password));
    userMapper.updateByUserIdSelective(user);
  }

  @Override
  public void updateFullName(Integer userid, String fullname, String operator) {
    User userMp = userMapper.selectByUserId(userid);
    if (userMp == null) {
      throw new BizException(SysErrorEnum.USER_IS_NOT_NULL);
    }
    User user = new User();
    user.setId(userid);
    user.setEditor(operator);
    user.setName(fullname);
    userMapper.updateByUserIdSelective(user);
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
    user.setStatus(userStatusEnum.getCode());
    userMapper.updateByPrimaryKeySelective(user);
  }

  @Override
  public List<UserDTO> queryAll() {
    return UserDTO.toListDTO(userMapper.findAll());
  }

  @Override
  public UserDTO qyeryByUserName(String userName) {
    User user = userMapper.selectByUsername(userName);
    return UserDTO.toDTO(user);
  }

  @Override
  public UserDTO qyeryByUserId(Integer userid) {
    User user = userMapper.selectByUserId(userid);
    return UserDTO.toDTO(user);
  }

}
