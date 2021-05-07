package com.flink.streaming.web.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020/11/09
 * @time 22:13
 */
@Slf4j
public class HttpServiceCheckerUtil {

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
            if (e.getCause() instanceof ConnectException || e.getCause() instanceof SocketTimeoutException) {
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
        String url = "http://pre-hadoop-master002:8088/xxx";
        System.out.println(HttpServiceCheckerUtil.checkUrlConnect(url));
        ;

    }

}
