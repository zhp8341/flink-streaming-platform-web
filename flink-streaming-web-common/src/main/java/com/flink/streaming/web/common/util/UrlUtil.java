package com.flink.streaming.web.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/28
 * @time 14:12
 */
@Slf4j
public class UrlUtil {

  public static String downLoadFromUrl(String urlStr, String savePath) throws Exception {
    URL url = new URL(urlStr);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    //设置超时间为3秒
    conn.setConnectTimeout(HttpUtil.TIME_OUT_1_M);
    //防止屏蔽程序抓取而返回403错误
    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
    //得到输入流
    InputStream inputStream = conn.getInputStream();
    //获取自己数组
    byte[] getData = readInputStream(inputStream);

    //文件保存位置
    File saveDir = new File(savePath);
    if (!saveDir.exists()) {
      log.info("创建文件夹 {}", savePath);
      saveDir.mkdirs();
    }
    String fileName = MatcherUtils.lastUrlValue(urlStr);

    String pathName = saveDir + File.separator + fileName;

    File file = new File(pathName);
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(getData);
    if (fos != null) {
      fos.close();
    }
    if (inputStream != null) {
      inputStream.close();
    }
    log.info("文件下载成功 {}", pathName);
    return pathName;
  }

  /**
   * 从输入流中获取字节数组
   *
   * @param inputStream
   * @return
   * @throws IOException
   */
  public static byte[] readInputStream(InputStream inputStream) throws IOException {
    byte[] buffer = new byte[1024];
    int len = 0;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((len = inputStream.read(buffer)) != -1) {
      bos.write(buffer, 0, len);
    }
    bos.close();
    return bos.toByteArray();
  }

  public static void main(String[] args) {
    try {
      downLoadFromUrl("http://ccblog.cn/jars/flink-streaming-udf.jar",
          "/Users/huipeizhu/logs/");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
