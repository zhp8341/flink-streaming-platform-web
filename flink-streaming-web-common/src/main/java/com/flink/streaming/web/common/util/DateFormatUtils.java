package com.flink.streaming.web.common.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-04
 * @time 00:20
 */
public class DateFormatUtils {


  public static final String FORMAT_FULL_TIME_NO_ZONE = "yyyy-MM-dd HH:mm:ss";

  public static Date toFormatDate(String dateTimeStr) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(FORMAT_FULL_TIME_NO_ZONE);
    DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
    return dateTime.toDate();
  }

  public static String toFormatString(Date date) {
    return new DateTime(date).toString(FORMAT_FULL_TIME_NO_ZONE);
  }

  public static String toFormatString(Date date, String pattern) {
    return new DateTime(date).toString(pattern);
  }
}
