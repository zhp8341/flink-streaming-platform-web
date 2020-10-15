package com.flink.streaming.web.service;

import com.flink.streaming.web.base.TestRun;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

}
