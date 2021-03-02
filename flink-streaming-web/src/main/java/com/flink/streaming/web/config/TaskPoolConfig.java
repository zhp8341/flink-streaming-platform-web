package com.flink.streaming.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * SchedulerTask 线程池
 * @author zhuhuipei
 * @Description:
 * @date 2018/9/6
 * @time 下午21:58
 */
@Configuration
@EnableAsync
public class TaskPoolConfig {

    private int corePoolSize = 25;

    private int maxPoolSize = 50;

    private int queueCapacity = 10;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
