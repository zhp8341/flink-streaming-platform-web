package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.util.FileUtils;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.service.SystemConfigService;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/21
 */

@RestController
@RequestMapping("/readLocal")
@Slf4j
public class ReadLocalDataApiController {

  @Autowired
  private SystemConfigService systemConfigService;

  @RequestMapping("/{fileName}")
  public void download(@PathVariable("fileName") String fileName, HttpServletResponse response) {

    try {
      ServletOutputStream outputStream = response.getOutputStream();
      String sqlPath = FileUtils.getSqlHome(systemConfigService
          .getSystemConfigByKey(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey()))
          + fileName;
      if (StringUtils.isEmpty(sqlPath)) {
        outputStream.write("uploadFileDTO is null".getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        return;
      }
      File file = new File(sqlPath);
      if (!file.exists()) {
        outputStream.write("file is not exists".getBytes(StandardCharsets.UTF_8));
        outputStream.close();
        return;
      }
      InputStream inputStream = new FileInputStream(sqlPath);
      String filename = file.getName();
      response.addHeader("Content-Disposition",
          "attachment; filename=" + URLEncoder.encode(filename, SystemConstants.CODE_UTF_8));
      byte[] b = new byte[1024];
      int len;
      while ((len = inputStream.read(b)) > 0) {
        outputStream.write(b, 0, len);
      }
      inputStream.close();
      outputStream.close();
    } catch (Exception ex) {
      log.error("download is error ", ex);
    }
  }

}
