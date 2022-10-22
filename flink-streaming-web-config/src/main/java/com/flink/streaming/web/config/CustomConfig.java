package com.flink.streaming.web.config;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.web.common.util.IpUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/22
 * @time 22:50
 */
@Configuration
@Data
public class CustomConfig {


  @Value("${server.port}")
  private Integer webPort;


  private String localUrl;

  public String getHttpLocalUrl() {
    return "http://127.0.0.1:" + webPort + SystemConstant.VIRGULE;
  }

  public String getUrlForDown() {
    return String.format("http://%s:%s/download/", IpUtil.getInstance().getHostIp(), webPort);
  }

  public String getUrlForReadLocal() {
    return String.format("http://%s:%s/readLocal/", IpUtil.getInstance().getHostIp(), webPort);
  }

}
