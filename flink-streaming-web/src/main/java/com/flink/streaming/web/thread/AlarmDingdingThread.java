package com.flink.streaming.web.thread;

import com.flink.streaming.web.ao.AlarmServiceAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/2/21
 * @time 17:37
 */
@Slf4j
public class AlarmDingdingThread implements Runnable {

    private AlarmServiceAO alarmServiceAO;

    private String content;

    private Long jobConfigId;

    private String url;


    public AlarmDingdingThread(AlarmServiceAO alarmServiceAO, String content, Long jobConfigId, String url) {
        this.alarmServiceAO = alarmServiceAO;
        this.content = content;
        this.jobConfigId = jobConfigId;
        this.url = url;
    }

    @Override
    public void run() {
        try {
            log.info("开始执行钉钉告警 content={} jobConfigId={}",content,jobConfigId);
            if (StringUtils.isEmpty(url)) {
                log.warn("没有配置钉钉url地址 不发送告警");
                return;
            }
            alarmServiceAO.sendForDingding(url, content, jobConfigId);
        } catch (Exception e) {
            log.error("告警失败 is error", e);
        }
    }
}
