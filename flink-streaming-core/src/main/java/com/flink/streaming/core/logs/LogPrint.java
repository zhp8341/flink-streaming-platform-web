package com.flink.streaming.core.logs;

import com.flink.streaming.common.enums.SqlCommand;
import com.flink.streaming.common.model.SqlCommandCall;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.TableEnvironment;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/21
 * @time 22:20
 */
@Slf4j
public class LogPrint {

  /**
   * 打印SqlCommandCall 日志信息
   *
   * @author zhuhuipei
   * @date 2021/3/21
   * @time 11:25
   */
  public static void logPrint(SqlCommandCall sqlCommandCall) {
    if (sqlCommandCall == null) {
      throw new NullPointerException("sqlCommandCall is null");
    }
    switch (sqlCommandCall.getSqlCommand()) {
      case SET:
        System.out.println(
            "\n############# " + sqlCommandCall.getSqlCommand().name() + " ############# \nSET "
                + sqlCommandCall.getOperands()[0] + "=" + sqlCommandCall.getOperands()[1]);
        log.info("\n############# {} ############# \nSET{}={}",
            sqlCommandCall.getSqlCommand().name(), sqlCommandCall.getOperands()[0],
            sqlCommandCall.getOperands()[1]);
        break;
      default:
        System.out.println(
            "\n############# " + sqlCommandCall.getSqlCommand().name() + " ############# \n"
                + sqlCommandCall.getOperands()[0]);
        log.info("\n############# {} ############# \n {}", sqlCommandCall.getSqlCommand().name(),
            sqlCommandCall.getOperands()[0]);
    }
  }

  /**
   * show 语句  select语句结果打印
   *
   * @author zhuhuipei
   * @date 2021/3/21
   * @time 11:23
   */
  public static void queryRestPrint(TableEnvironment tEnv, SqlCommandCall sqlCommandCall) {
    if (sqlCommandCall == null) {
      throw new NullPointerException("sqlCommandCall is null");
    }
    LogPrint.logPrint(sqlCommandCall);

    if (sqlCommandCall.getSqlCommand().name().equalsIgnoreCase(SqlCommand.SELECT.name())) {
      throw new RuntimeException("目前不支持select 语法使用");
    } else {
      tEnv.executeSql(sqlCommandCall.getOperands()[0]).print();
    }

//        if (sqlCommandCall.getSqlCommand().name().equalsIgnoreCase(SqlCommand.SELECT.name())) {
//            Iterator<Row> it = tEnv.executeSql(sqlCommandCall.operands[0]).collect();
//            while (it.hasNext()) {
//                String res = String.join(",", PrintUtils.rowToString(it.next()));
//                log.info("数据结果 {}", res);
//            }
//        }
  }

}
