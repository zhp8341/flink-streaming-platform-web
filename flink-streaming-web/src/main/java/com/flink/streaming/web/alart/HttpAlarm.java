package com.flink.streaming.web.alart;

import com.flink.streaming.web.model.vo.CallbackDTO;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/2/21
 * @time 11:38
 */
public interface HttpAlarm {

    /**
     * 回调http
     *
     * @author zhuhuipei
     * @date 2021/2/21
     * @time 11:39
     */
    boolean send(String url, CallbackDTO callbackDTO);
}
