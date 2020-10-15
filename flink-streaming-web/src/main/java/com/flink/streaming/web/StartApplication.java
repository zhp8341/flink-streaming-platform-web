package com.flink.streaming.web;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 管理界面启动类
 *
 * @author zhuhuipei
 * @date 2020-07-10
 * @time 01:20
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.flink.streaming.web.mapper")
public class StartApplication {

    public static void main(String[] args) {
        log.info("##########web服务开始启动############");
        SpringApplication.run(StartApplication.class, args);
        log.info("##########web服务完毕############");
    }

}
