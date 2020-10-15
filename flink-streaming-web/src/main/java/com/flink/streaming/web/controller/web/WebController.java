package com.flink.streaming.web.controller.web;

import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.service.SystemConfigService;
import com.flink.streaming.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;


    @RequestMapping("/index")
    public String index(ModelMap modelMap, String message) {
        message = StringUtils.isEmpty(message) ? "" : message;
        modelMap.put("message", message);
        return "screen/login";
    }

    @RequestMapping("/login")
    public String login(ModelMap modelMap, HttpServletResponse response, String name, String password) {

        try {
            String cookieId = userService.login(name, password);
            if (StringUtils.isEmpty(cookieId)) {
                return index(modelMap, "登录失败");
            }
            Cookie cookie = new Cookie(SystemConstants.COOKIE_NAME_SESSION_ID, cookieId);
            //24小时有效
            cookie.setMaxAge(24 * 60 * 60);
            //全局有效
            cookie.setPath("/");
            response.addCookie(cookie);
            log.info("cookieId={},name={}", cookieId, name);

            return "redirect:/admin/listPage";
        } catch (BizException e) {
            log.warn("login is error ", e);
            return index(modelMap, "登录失败:" + e.getErrorMsg());
        }

    }


}
