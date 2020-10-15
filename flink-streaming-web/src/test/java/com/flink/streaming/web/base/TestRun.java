package com.flink.streaming.web.base;

import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-10
 * @time 00:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("com.flink.streaming.web.mapper")
public class TestRun {
}
