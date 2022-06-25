package com.flink.streaming.sql.validation.test;


import com.flink.streaming.common.sql.SqlFileParser;
import com.flink.streaming.sql.validation.SqlValidation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 22:30
 */
public class TestSqlValidation {

  private static String test_sql_file = "/Users/edy/git/flink-streaming-platform-web/flink-streaming-core/src/hive-test.sql";

  @Test
  public void checkSql() throws IOException {
    List<String> list = Files.readAllLines(Paths.get(test_sql_file));
    List<String> sqlList = SqlFileParser.parserSql(list);
    SqlValidation.explainStmt(sqlList);
    //SqlValidation.preCheckSql(list);
  }
}
