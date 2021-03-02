package com.flink.streaming.web.thread;

import com.flink.streaming.web.ao.AlarmServiceAO;
import com.flink.streaming.web.model.vo.CallbackDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/2/21
 * @time 17:37
 */
@Slf4j
public class AlarmHttpThread implements Runnable {

    private AlarmServiceAO alarmServiceAO;

    private CallbackDTO callbackDTO;

    private String url;

    public AlarmHttpThread(AlarmServiceAO alarmServiceAO, CallbackDTO callbackDTO, String url) {
        this.alarmServiceAO = alarmServiceAO;
        this.callbackDTO = callbackDTO;
        this.url = url;
    }

    @Override
    public void run() {
        try {
            log.info("开始执行http回调告警 callbackDTO={} url={}", callbackDTO, url);
            if (StringUtils.isEmpty(url)) {
                log.warn("没有配置http回调地址 不发送告警");
                return;
            }
            alarmServiceAO.sendForHttp(url, callbackDTO);
        } catch (Exception e) {
            log.error("告警失败 is error", e);
        }
    }
}
