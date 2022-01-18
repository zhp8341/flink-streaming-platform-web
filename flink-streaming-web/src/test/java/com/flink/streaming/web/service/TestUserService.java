package com.flink.streaming.web.service;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.enums.UserStatusEnum;
import com.flink.streaming.web.model.dto.UserDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 23:41
 */
public class TestUserService extends TestRun {

    @Autowired
    private UserService userService;

    @Test
    public void checkLogin() {
        String cookieId = userService.login("admin", "123456");
        Assert.assertNotNull(cookieId);
    }

    @Test
    public void add() {
       // userService.addUser("root", "123456", "test");
    }


    @Test
    public void updatePassword() {
        userService.updatePassword("root", "123456","123456", "test");
    }

    @Test
    public void stopOrOpen() {
        userService.stopOrOpen("root", UserStatusEnum.CLOSE, "test");
    }

    @Test
    public void queryAll() {
        List<UserDTO> list = userService.queryAll();
        System.out.println(list);
    }


}
