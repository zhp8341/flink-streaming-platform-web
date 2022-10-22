package com.flink.streaming.web.controller.api;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.util.UserSessionUtil;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.dto.UserDTO;
import com.flink.streaming.web.model.dto.UserSession;
import com.flink.streaming.web.model.page.PageParam;
import com.flink.streaming.web.model.vo.Constant;
import com.flink.streaming.web.model.vo.PageVO;
import com.flink.streaming.web.model.vo.UserVO;
import com.flink.streaming.web.service.UserService;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class UserApiController extends BaseController {

  @Autowired
  private UserService userService;


  @RequestMapping("/login")
  public RestResult login(ModelMap modelMap, HttpServletResponse response, String name,
      String password) {
    try {
      String cookieId = userService.login(name, password);
      if (StringUtils.isEmpty(cookieId)) {
        return RestResult.error("登录失败，请联系查看日志");
      }
      Cookie cookie = new Cookie(SystemConstants.COOKIE_NAME_SESSION_ID, cookieId);
      //24小时有效
      cookie.setMaxAge(24 * 60 * 60);
      //全局有效
      cookie.setPath(SystemConstant.VIRGULE);
      response.addCookie(cookie);
      log.info("cookieId={},name={}", cookieId, name);
    } catch (Exception e) {
      log.warn("login is error ", e);
      return RestResult.error(e.getMessage());
    }
    UserDTO user = userService.qyeryByUserName(name);
    return RestResult.newInstance(Constant.RESPONE_STATUS_SUCCESS, "登录成功！", UserVO.toVO(user));
  }

  @RequestMapping("/logout")
  public RestResult logout(HttpServletRequest request, HttpServletResponse response) {
    try {
      UserSession userSession = UserSessionUtil.userSession(request);
      boolean nologin = (userSession == null) || (!userService.checkLogin(userSession));
      if (nologin) {
        RestResult.newInstance(Constant.RESPONE_STATUS_SUCCESS, "没有登录或找不到用户信息！", null);
      }
      UserDTO user = userService.qyeryByUserName(userSession.getName());
      //清除Cookies信息
      Cookie cookies[] = request.getCookies();
      for (Cookie cookie : cookies) {
        Cookie clearCookie = new Cookie(cookie.getName(), null);
        clearCookie.setMaxAge(0);
        clearCookie.setPath(cookie.getPath());
        response.addCookie(cookie);
      }
      return RestResult.newInstance(Constant.RESPONE_STATUS_SUCCESS, "退出成功！", UserVO.toVO(user));
    } catch (Exception e) {
      log.warn("login is error ", e);
      return RestResult.error(e.getMessage());
    }
  }

  /**
   * 分页查询所有用户列表
   *
   * @param modelMap
   * @param pageparam
   * @return
   * @author wxj
   * @date 2021年12月14日 下午4:21:15
   * @version V1.0
   */
  @RequestMapping("/userList")
  public RestResult<?> userList(ModelMap modelMap, PageParam pageparam) {
    if (pageparam == null) {
      pageparam = new PageParam();
    }
    PageModel<UserDTO> list = userService.queryAllByPage(pageparam);
    List<UserVO> vlist = UserVO.toListVO(list);
    PageVO pageVO = new PageVO();
    pageVO.setPageNum(list.getPageNum());
    pageVO.setPages(list.getPages());
    pageVO.setPageSize(list.getPageSize());
    pageVO.setTotal(list.getTotal());
    pageVO.setData(vlist);
    return RestResult.success(pageVO);
  }

  /**
   * 获取当前用户信息
   *
   * @param request
   * @return
   * @author wxj
   * @date 2021年11月30日 下午3:22:00
   * @version V1.0
   */
  @RequestMapping("/getUserInfo")
  public RestResult<?> getUserInfo(HttpServletRequest request) {
    try {
      UserSession userSession = UserSessionUtil.userSession(request);
      boolean nologin = (userSession == null) || (!userService.checkLogin(userSession));
      if (nologin) {
        return RestResult.error("登录失败，请联系查看日志");
      }
      UserDTO user = userService.qyeryByUserName(userSession.getName());
      return RestResult.newInstance(Constant.RESPONE_STATUS_SUCCESS, "登录成功！", UserVO.toVO(user));
    } catch (Exception e) {
      log.warn("login is error ", e);
      return RestResult.error(e.getMessage());
    }
  }

  @RequestMapping(value = "/addUser", method = RequestMethod.POST)
  public RestResult addUser(String name, String fullname, String pwd1, String pwd2) {
    try {
      this.check(name, pwd1, pwd2);
      userService.addUser(name, fullname, pwd1, this.getUserName());
    } catch (Exception e) {
      log.warn("新增账号失败", e);
      return RestResult.error(e.getMessage());
    }
    return RestResult.success();
  }

  @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
  public RestResult updatePassword(String name, String fullname, String oldPwd, String pwd1,
      String pwd2) {
    try {
      this.check(name, pwd1, pwd2);
      userService.updatePassword(name, oldPwd, pwd1, this.getUserName());
    } catch (Exception e) {
      log.warn("修改密码失败", e);
      return RestResult.error(e.getMessage());
    }
    return RestResult.success();
  }

  /**
   * 修改当前用户密码
   *
   * @param request
   * @param userid
   * @param password
   * @return
   * @author wxj
   * @date 2021年12月1日 下午2:09:34
   * @version V1.0
   */
  @RequestMapping(value = "/updateCurrentUserPassword", method = RequestMethod.POST)
  public RestResult updateCurrentUserPassword(HttpServletRequest request, Integer userid,
      String password) {
    try {
      UserSession userSession = UserSessionUtil.userSession(request);
      boolean nologin = (userSession == null) || (!userService.checkLogin(userSession));
      if (nologin) {
        RestResult.newInstance(Constant.RESPONE_STATUS_SUCCESS, "没有登录或找不到用户信息！", null);
      }
      this.check(userSession.getName(), password, password);
      UserDTO user = userService.qyeryByUserId(userid);
      if (user.getId() != userSession.getUserid()) {
        return RestResult.error("不能修改非当前登录用户的密码！");
      }
      userService.updatePassword(userid, password, this.getUserName());
    } catch (Exception e) {
      log.warn("修改密码失败", e);
      return RestResult.error(e.getMessage());
    }
    return RestResult.success();
  }

  /**
   * 修改用户信息
   *
   * @param username
   * @param name
   * @return
   * @author wxj
   * @date 2021年12月1日 上午10:19:56
   * @version V1.0
   */
  @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
  public RestResult updateUserInfo(Integer userid, String fullname) {
    try {
      userService.updateFullName(userid, fullname, this.getUserName());
    } catch (Exception e) {
      log.warn("修改用户信息失败", e);
      return RestResult.error(e.getMessage());
    }
    return RestResult.success();
  }

  @RequestMapping(value = "/stopOrOpen", method = RequestMethod.POST)
  public RestResult stopOrOpen(String name, Integer code) {
    try {
      userService.stopOrOpen(name, UserStatusEnum.getStatus(code), this.getUserName());
    } catch (Exception e) {
      log.warn("操作失败联系管理员", e);
      return RestResult.error(e.getMessage());
    }
    return RestResult.success();
  }


  private void check(String name, String pwd1, String pwd2) {
    if (StringUtils.isEmpty(name)) {
      throw new BizException("账号不能为空");
    }
    if (name.length() < 4) {
      throw new BizException("名称长度不能少于4位");
    }
    if (StringUtils.isEmpty(pwd1) || StringUtils.isEmpty(pwd2)) {
      throw new BizException("密码和确认密码都不能为空");
    }
    if (!pwd1.equals(pwd2)) {
      throw new BizException("二次密码输入不一致");
    }
    if (pwd1.length() < 6) {
      throw new BizException("密码长度不能少于6位");
    }
    if (!name.matches("[a-zA-Z0-9]+")) {
      throw new BizException("账号只能是英文字母或数字");
    }
  }

}
