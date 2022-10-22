package com.flink.streaming.web.common.util;

import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020/11/09
 * @time 22:13
 */
@Slf4j
public class HttpServiceCheckerUtil {

  public static final int TIMEOUTMILLSECONDS = 2000;

  /**
   * 检查url地址连接是否正常
   *
   * @author zhuhuipei
   * @date 2020/11/09
   * @time 22:45
   */
  public static boolean checkUrlConnect(String url) {
    try {
      log.info("checkUrlConnect url is {}", url);
      RestTemplate restTemplate = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_20_S);
      restTemplate.exchange(url.trim(), HttpMethod.GET, new HttpEntity<String>(null,
          new HttpHeaders()), String.class);
    } catch (ResourceAccessException e) {
      if (e.getCause() instanceof ConnectException || e
          .getCause() instanceof SocketTimeoutException) {
        log.error("[checkUrlConnect]网络异常或者超时 url={}", url, e);
        return false;
      } else {
        log.warn("[checkUrlConnect]url={} 出错了 {}", e);
        return false;
      }
    } catch (Exception e) {
      log.error("[checkUrlConnect]url={} 出错了 {}", e);
      return false;
    }
    log.info("网络检查正常 url={}", url);
    return true;
  }


  public static void main(String[] args) {
//        String url = "https://youtube.com/";
    String url = "http://192.168.1.113:8088/";
//    System.out.println(HttpServiceCheckerUtil.isConnect(url));

    String request = cn.hutool.http.HttpUtil
        .get("http://192.168.1.113:8088/" + FlinkYarnRestUriConstants.URI_YARN_INFO);
    System.out.println(request);
  }

}
