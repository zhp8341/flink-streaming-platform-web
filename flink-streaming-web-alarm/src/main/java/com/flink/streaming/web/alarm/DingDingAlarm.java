package com.flink.streaming.web.alarm;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-23
 * @time 23:59
 */
public interface DingDingAlarm {


  /**
   * @author zhuhuipei
   * @date 2020-09-25
   * @time 23:02
   */
  boolean send(String url, String content);

}
