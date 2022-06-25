package com.flink.streaming.web.service;

import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.dto.UserDTO;
import com.flink.streaming.web.model.dto.UserSession;
import com.flink.streaming.web.model.page.PageParam;
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
  void addUser(String userName, String fullname, String password, String operator);


  /**
   * 修改密码
   *
   * @author zhuhuipei
   * @date 2020/11/11
   * @time 23:00
   */
  void updatePassword(String userName, String oldPassword, String newPassword, String operator);

  /**
   * 修改密码
   *
   * @param userId
   * @param password
   * @param operator
   * @author wxj
   * @date 2021年12月1日 下午1:56:14
   * @version V1.0
   */
  void updatePassword(Integer userId, String password, String operator);

  /**
   * 修改用户名称
   *
   * @param userName
   * @param name
   * @param operator
   * @author wxj
   * @date 2021年12月1日 上午10:17:26
   * @version V1.0
   */
  void updateFullName(Integer userid, String fullname, String operator);

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

  /**
   * 获取全部账号(分布)
   *
   * @param pageparam
   * @return
   * @author wxj
   * @date 2021年12月14日 下午4:01:57
   * @version V1.0
   */
  PageModel<UserDTO> queryAllByPage(PageParam pageparam);

  /**
   * 根据用户名称查询用户
   *
   * @param userName
   * @return
   * @author wxj
   * @date 2021年11月30日 上午11:45:36
   * @version V1.0
   */
  UserDTO qyeryByUserName(String userName);

  /**
   * 根据用户编号查询用户
   *
   * @param userName
   * @return
   * @author wxj
   * @date 2021年11月30日 上午11:45:36
   * @version V1.0
   */
  UserDTO qyeryByUserId(Integer userid);
}
