package com.flink.streaming.web.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 23:05
 */
@Slf4j
public class HttpUtil {


    public final static int TIME_OUT_30_S=1000 * 30;

    public final static int TIME_OUT_10_S=1000 * 10;

    public final static int TIME_OUT_3_S=1000 * 3;

    public final static int TIME_OUT_5_M=1000 * 60 *5;

    public final static int TIME_OUT_1_M=1000 * 60 *1;



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


    public static boolean isHttpsOrHttp(String url){

        String regUrl="^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+$";
        Pattern p = Pattern.compile(regUrl);
        Matcher m = p.matcher(url.trim());
        if(!m.matches()){
            return false;
        }
        return true;
    }


    public static void downFileByCurl(String[] cmds) throws Exception {
        log.info("下载文件：{}",cmds);
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p = process.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
        int rs = p.waitFor();
        if (rs != 0) {
            throw new Exception(" downFileByCurl pcs.waitFor() is error  rs=" + rs);
        }
    }


    public static void main(String[] args){
       System.out.println(isHttpsOrHttp(" http://cloud.yangtuojia.com/bigdata/flink-sql-connector-elasticsearch6_2.11-1.12.0.jar "));
    }

}
