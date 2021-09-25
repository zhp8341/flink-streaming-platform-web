package com.flink.streaming.web;

import cn.hutool.core.io.IoUtil;
import com.flink.streaming.web.common.SystemConstants;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

/**
 * 管理界面启动类
 *
 * @author zhuhuipei
 * @date 2020-07-10
 * @time 01:20
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.flink.streaming.web.mapper")
public class StartApplication {

  public static void main(String[] args) throws Exception {
    log.info("##########web服务开始启动############");
    readBanner();
    SpringApplication.run(StartApplication.class, args);
    log.info("##########web服务完毕############");
  }

  private static void readBanner() {
    BufferedReader reader = null;
    InputStream inputStream = null;
    try {
      ClassPathResource classPathResource = new ClassPathResource("cus_banner.txt");
      inputStream = classPathResource.getInputStream();
      reader = new BufferedReader(
          new InputStreamReader(classPathResource.getInputStream(), SystemConstants.CODE_UTF_8));
      String result = null;
      //按行读取
      while ((result = reader.readLine()) != null) {
        log.info(result);
      }
      reader.close();
      classPathResource.getInputStream().close();
    } catch (Exception e) {

      log.error("read readBanner is error", e);
    } finally {
      IoUtil.close(reader);
      IoUtil.close(inputStream);
    }
  }
}
