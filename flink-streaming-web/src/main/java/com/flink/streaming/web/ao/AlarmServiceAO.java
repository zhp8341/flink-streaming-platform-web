package com.flink.streaming.web.ao;

import com.flink.streaming.web.model.vo.CallbackDTO;

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


    /**
     * 发送http请求回调
     *
     * @author zhuhuipei
     * @date 2021/2/21
     * @time 11:31
     */
    boolean sendForHttp(String url,CallbackDTO callbackDTO);
}
