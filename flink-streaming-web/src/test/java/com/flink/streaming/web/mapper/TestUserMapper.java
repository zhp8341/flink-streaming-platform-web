package com.flink.streaming.web.mapper;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.model.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-10
 * @time 00:09
 */
public class TestUserMapper extends TestRun {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void getUser() {
        User user = userMapper.selectByUsername("admin");
        Assert.assertNotNull(user);
    }
}
