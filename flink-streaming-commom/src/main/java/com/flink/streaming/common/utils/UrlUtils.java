package com.flink.streaming.common.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/21
 */
public class UrlUtils {

  private static final String REG_1 = "^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\\\/])+$";


  public static boolean isHttpsOrHttp(String url) {
    Pattern p = Pattern.compile(REG_1);
    Matcher m = p.matcher(url.trim());
    if (!m.matches()) {
      return false;
    }
    return true;
  }
  public static List<String> getSqlList(String sqlUrl) {
    List<String> fileList = new ArrayList<String>();
    try {
      URL url = new URL(sqlUrl);
      InputStream in = url.openStream();
      InputStreamReader isr = new InputStreamReader(in);
      BufferedReader bufr = new BufferedReader(isr);
      String str;
      while ((str = bufr.readLine()) != null) {
        fileList.add(str);
      }
      bufr.close();
      isr.close();
      in.close();
      return fileList;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }
}
