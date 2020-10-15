package com.flink.streaming.web.ao;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 19:50
 */
public interface AlarmServiceAO {

    /**
     * 发送钉钉告警消息
     *
     * @author zhuhuipei
     * @date 2020-09-25
     * @time 19:53
     */
    boolean sendForDingding(String url, String content, Long jobConfigId);
}
