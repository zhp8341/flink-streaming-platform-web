package com.flink.streaming.web.alarm.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flink.streaming.web.alarm.DingDingAlarm;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.exceptions.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-23
 * @time 01:01
 */
@Slf4j
@Component
public class DingDingAlarmImpl implements DingDingAlarm {


  @Override
  public boolean send(String url, String content) {

    String res = null;
    try {
      log.info("开始发送钉钉消息 url ={} content={}", url, content);
      if (StringUtils.isEmpty(url) || StringUtils.isEmpty(content)) {
        log.error("url or content is null url={} content={}", url, content);
        throw new BizException("请求参数url or content is null");
      }

      HttpHeaders httpHeaders = HttpUtil.buildHttpHeaders(MediaType.APPLICATION_JSON_VALUE);
      HttpEntity<String> httpEntity = new HttpEntity(buildContent(content), httpHeaders);
      RestTemplate restTemplate = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_15_S);
      res = restTemplate.postForObject(url, httpEntity, String.class);
      if (StringUtils.isEmpty(res)) {
        throw new BizException("消息发送失败 res is null");
      }
      JSONObject jsonObject = JSON.parseObject(res);
      if (jsonObject == null || (jsonObject.get("errcode") != null && !"0"
          .equals(jsonObject.get("errcode").toString()))) {
        log.error("消息发送失败 url={},content={},res={}", url, content, res);
        throw new BizException("消息发送失败" + res);
      }
    } catch (Exception e) {
      log.error("消息发送失败 url={},content={},res={}", url, content, res, e);
      throw new BizException("消息发送失败" + e.getMessage());
    }

    return true;
  }


  private String buildContent(String text) {
    StringBuilder txt = new StringBuilder();
    txt.append("{\"msgtype\": \"text\", \"text\": { \"content\": \"");
    txt.append(DateUtil.date().toStringDefaultTimeZone() + " ");
    txt.append(text).append("\" } }");
    return txt.toString();
  }


}
