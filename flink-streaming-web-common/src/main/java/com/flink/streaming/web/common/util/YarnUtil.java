package com.flink.streaming.web.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-11
 * @time 01:49
 */
@Slf4j
public class YarnUtil {

  public static String getQueueName(String flinkRunConfig) throws ParseException {
    //CommandLine cl = CliConfigUtil.getFlinkRunByCli(flinkRunConfig);
//    String[] config = trim(flinkRunConfig);

//    return cl.getOptionValue(SystemConstants.YQU);
    String getYarnQueueName = CliConfigUtil.getYarnQueueName(flinkRunConfig);
    log.info("得到队列名称是：getYarnQueueName={}", getYarnQueueName);
    return getYarnQueueName;
  }

  
}
