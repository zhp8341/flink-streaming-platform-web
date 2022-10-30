package com.flink.streaming.web.common.util;

import com.flink.streaming.web.common.SystemConstants;
import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 加密
 *
 * @author zhuhuipei
 * @date 2020-07-13
 * @time 23:30
 */
@Slf4j
public class Md5Utils {

 private  static final char HEXDIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
     'A', 'B', 'C', 'D', 'E', 'F'};
  public static String getMD5String(String value) {

    return DigestUtils.md5Hex(value);

  }

  public static final String md5(String s) {

    try {
      byte[] btInput = s.getBytes(SystemConstants.CODE_UTF_8);
      MessageDigest mdInst = MessageDigest.getInstance("MD5");
      mdInst.update(btInput);
      byte[] md = mdInst.digest();

      int j = md.length;
      char str[] = new char[j * 2];
      int k = 0;
      for (int i = 0; i < j; i++) {
        byte byte0 = md[i];
        str[k++] = HEXDIGITS[byte0 >>> 4 & 0xf];
        str[k++] = HEXDIGITS[byte0 & 0xf];
      }
      return new String(str);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void main(String[] args) {

    System.out.println(getMD5String("123456"));
    System.out.println(getMD5String("123456"));

  }
}
