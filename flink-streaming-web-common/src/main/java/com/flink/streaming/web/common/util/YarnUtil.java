package com.flink.streaming.web.common.util;

import com.flink.streaming.web.common.SystemConstants;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-11
 * @time 01:49
 */
public class YarnUtil {

  public static String getQueueName(String flinkRunConfig) throws ParseException {
    CommandLine cl = CliConfigUtil.getFlinkRunByCli(flinkRunConfig);
    return cl.getOptionValue(SystemConstants.YQU);
  }


  public static void main(String[] args) throws ParseException {
    System.out
        .println(YarnUtil.getQueueName("-yqu streaming     -yjm    1024m    -ytm   2048m -p 1 "));
  }
}
