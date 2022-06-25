package com.flink.streaming.web.config;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2018/9/6
 * @time 下午21:58
 */

@Slf4j
public final class WaitForPoolConfig {

  private static int corePoolSize = 20;

  private static int maximumPoolSize = 400;

  private static long keepAliveTime = 10;


  private static ThreadPoolExecutor threadPoolExecutor;

  private static WaitForPoolConfig alarmPoolConfig;

  private WaitForPoolConfig() {
    BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100, true);
    threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
        TimeUnit.MINUTES,
        workQueue, new ThreadPoolExecutor.AbortPolicy());
  }

  public static synchronized WaitForPoolConfig getInstance() {
    if (null == alarmPoolConfig) {
      synchronized (WaitForPoolConfig.class) {
        if (null == alarmPoolConfig) {
          alarmPoolConfig = new WaitForPoolConfig();
        }
      }
    }
    log.info("WaitForPoolConfig threadPoolExecutor={}", threadPoolExecutor);
    return alarmPoolConfig;
  }

  public synchronized ThreadPoolExecutor getThreadPoolExecutor() {
    return threadPoolExecutor;
  }
}
