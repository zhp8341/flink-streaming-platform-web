package com.flink.streaming.web.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 23:05
 */
@Slf4j
public class HttpUtil {


  public static final int TIME_OUT_30_S = 1000 * 30;

  public static final int TIME_OUT_15_S = 1000 * 15;

  public static final int TIME_OUT_20_S = 1000 * 20;

  public static final int TIME_OUT_3_S = 1000 * 3;

  public static final int TIME_OUT_5_M = 1000 * 60 * 5;

  public static final int TIME_OUT_1_M = 1000 * 60 * 1;


  public static HttpHeaders buildHttpHeaders(String contentType) {
    HttpHeaders httpHeaders = new HttpHeaders();
    if (StringUtils.isEmpty(contentType)) {
      httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    } else {
      httpHeaders.add("Content-Type", contentType);
    }
    httpHeaders.add("X-Requested-With", "XMLHttpRequest");
    httpHeaders.add("Accept", "text/plain;charset=utf-8");
    return httpHeaders;
  }

  public static RestTemplate buildRestTemplate(Integer connectTimeout) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeout);
    requestFactory.setReadTimeout(connectTimeout);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    return restTemplate;
  }


  public static String buildUrl(String domain, String urn) {

    return new StringBuilder(domain).append(urn).toString();
  }

}
