package com.flink.streaming.web.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 核心线程10个 最大线程100 队列100 公平策略
 * 告警线程池
 * @author zhuhuipei
 * @Description:
 * @date 2018/9/6
 * @time 下午21:58
 */

@Slf4j
public class AlarmPoolConfig {

    private static int corePoolSize = 10;

    private static int maximumPoolSize = 30;

    private static long keepAliveTime = 10;


    private static ThreadPoolExecutor threadPoolExecutor;

    private static AlarmPoolConfig alarmPoolConfig;

    private AlarmPoolConfig() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100, true);
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MINUTES, workQueue);
    }

    public static synchronized AlarmPoolConfig getInstance() {
        if (null == alarmPoolConfig) {
            synchronized (AlarmPoolConfig.class) {
                if (null == alarmPoolConfig) {
                    alarmPoolConfig = new AlarmPoolConfig();
                }
            }
        }
        log.info("AlarmPoolConfig threadPoolExecutor={}", threadPoolExecutor);
        return alarmPoolConfig;
    }

    public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
