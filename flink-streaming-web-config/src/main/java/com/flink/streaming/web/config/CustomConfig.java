package com.flink.streaming.web.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/22
 * @time 22:50
 */
@Configuration
@Data
public class CustomConfig {

    @Value("${custom.core.jar.name}")
    private String coreJarName;

    @Value("${server.port}")
    private  Integer webPort;
}
