package com.flink.streaming.web.controller.web;


import com.flink.streaming.web.model.vo.UserVO;
import com.flink.streaming.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping("/userList")
    public String userList(ModelMap modelMap) {
        modelMap.put("userList", UserVO.toListVO(userService.queryAll()));
        modelMap.put("active", "userlist");
        modelMap.put("open", "user");
        return "screen/user/userlist";
    }


    @RequestMapping("/editUser")
    public String editUser(ModelMap modelMap, String name) {
        modelMap.put("username", name);
        modelMap.put("active", "userlist");
        modelMap.put("open", "user");
        return "screen/user/editUser";
    }


    private String decode(String message) {
        try {
            return java.net.URLDecoder.decode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "error";
        }
    }


}
