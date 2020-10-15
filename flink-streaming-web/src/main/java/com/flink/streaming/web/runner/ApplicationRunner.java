package com.flink.streaming.web.runner;

import com.flink.streaming.web.service.IpStatusService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2018/5/9
 * @time 下午4:12
 */
@Component
@Order(0)
@Slf4j
@Configuration
@Getter
public class ApplicationRunner implements org.springframework.boot.ApplicationRunner {


    @Autowired
    private IpStatusService ipStatusService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        ipStatusService.registerIp();
    }


}
