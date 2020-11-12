package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
    public RestResult login(ModelMap modelMap, HttpServletResponse response, String name, String password) {
        try {
            String cookieId = userService.login(name, password);
            if (StringUtils.isEmpty(cookieId)) {
                return RestResult.error("登录失败，请联系查看日志");
            }
            Cookie cookie = new Cookie(SystemConstants.COOKIE_NAME_SESSION_ID, cookieId);
            //24小时有效
            cookie.setMaxAge(24 * 60 * 60);
            //全局有效
            cookie.setPath("/");
            response.addCookie(cookie);
            log.info("cookieId={},name={}", cookieId, name);
        } catch (Exception e) {
            log.warn("login is error ", e);
            return RestResult.error(e.getMessage());
        }
        return RestResult.success();
    }


    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public RestResult addUser(String name, String pwd1, String pwd2) {
        try {
            this.check(name, pwd1, pwd2);
            userService.addUser(name, pwd1, this.getUserName());
        } catch (Exception e) {
            log.warn("新增账号失败", e);
            return RestResult.error(e.getMessage());
        }
        return RestResult.success();
    }

    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    public RestResult updatePassword(String name, String oldPwd, String pwd1, String pwd2) {
        try {
            this.check(name, pwd1, pwd2);
            userService.updatePassword(name, oldPwd, pwd1, this.getUserName());
        } catch (Exception e) {
            log.warn("修改密码失败", e);
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
        if (!name.matches("[a-zA-Z]+")) {
            throw new BizException("账号只能是英文字母");
        }
    }

}
