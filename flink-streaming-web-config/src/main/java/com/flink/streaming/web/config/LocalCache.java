package com.flink.streaming.web.config;


import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.shaded.guava30.com.google.common.cache.Cache;
import org.apache.flink.shaded.guava30.com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/21
 */
@Configuration
@Slf4j
public class LocalCache {

  private Cache<String, String> cache = null;

  private static final long DURATION = 5;

  public LocalCache() {
    cache = CacheBuilder.newBuilder().expireAfterWrite(DURATION, TimeUnit.MINUTES).build();
  }


  public void put(String key, String value) {
    cache.put(key, value);
  }

  public String get(String key) {
    try {
      return cache.get(key, new Callable<String>() {
        @Override
        public String call() throws Exception {
          return "";
        }
      });
    } catch (Exception e) {
      log.error("get LocalCache error", e);
    }
    return null;

  }

}
