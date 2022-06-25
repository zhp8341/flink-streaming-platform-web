package com.flink.streaming.core.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.TableEnvironment;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 23:57
 */
@Slf4j
public class Configurations {


  /**
   * 单个设置Configuration
   *
   * @author zhuhuipei
   * @date 2021/3/23
   * @time 23:58
   */
  public static void setSingleConfiguration(TableEnvironment tEnv, String key, String value) {
    if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
      return;
    }
    Configuration configuration = tEnv.getConfig().getConfiguration();
    log.info("#############setConfiguration#############\n  key={} value={}", key, value);
    configuration.setString(key, value);

  }


}
