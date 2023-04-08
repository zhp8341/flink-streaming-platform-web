package com.flink.streaming.sql.validation;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.SqlCommand;
import com.flink.streaming.common.model.SqlCommandCall;
import com.flink.streaming.common.sql.SqlFileParser;
import com.flink.streaming.sql.util.ValidationConstants;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.sql.parser.validate.FlinkSqlConformance;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.config.TableConfigOptions;
import org.apache.flink.table.planner.calcite.CalciteConfig;
import org.apache.flink.table.planner.delegation.FlinkSqlParserFactories;
import org.apache.flink.table.planner.parse.CalciteParser;
import org.apache.flink.table.planner.utils.JavaScalaConversionUtil;
import org.apache.flink.table.planner.utils.TableConfigUtils;


/*
 *  数据校验
 * @Author: zhuhuipei
 * @date 2022/6/25
 */
@Slf4j
public class SqlValidation {


  public static void explainStmt(List<String> stmtList) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    EnvironmentSettings settings = EnvironmentSettings.newInstance()
        .inStreamingMode()
        .build();

    TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);
    TableConfig config = tEnv.getConfig();
    String sql = null;

    boolean isInsertSql = false;

    boolean isSelectSql = false;

    try {
      for (String stmt : stmtList) {
        sql = stmt.trim();
        Boolean setSuccess = setSqlDialect(sql, config);
        CalciteParser parser = new CalciteParser(getSqlParserConfig(config));
        if (setSuccess) {
          log.info("set 成功 sql={}", sql);
          continue;
        }
        SqlNode sqlNode = parser.parse(sql);
        if (ValidationConstants.INSERT.equalsIgnoreCase(sqlNode.getKind().name())) {
          isInsertSql = true;
        }
        if (ValidationConstants.SELECT.equalsIgnoreCase(sqlNode.getKind().name())) {
          isSelectSql = true;
        }
        log.info("sql:{} 校验通过", sql);
      }
    } catch (Exception e) {
      log.error("语法错误: {}  原因是: ", sql, e);
      throw new RuntimeException("语法错误:" + sql + "  原因:  " + e.getMessage());
    }
    if (!isInsertSql) {
      throw new RuntimeException(ValidationConstants.MESSAGE_010);
    }
    if (isSelectSql) {
      throw new RuntimeException(ValidationConstants.MESSAGE_011);
    }
    log.info("全部语法校验成功");

  }


  /**
   * @author zhuhuipei
   * @date 2021/3/27
   * @time 10:10
   */
  @Deprecated
  public static void preCheckSql(List<String> sql) {

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    EnvironmentSettings settings = EnvironmentSettings.newInstance()
        .inStreamingMode()
        .build();

    TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

    List<SqlCommandCall> sqlCommandCallList = SqlFileParser.fileToSql(sql);
    if (CollectionUtils.isEmpty(sqlCommandCallList)) {
      throw new RuntimeException("没解析出sql，请检查语句 如 缺少;号");
    }

    TableConfig config = tEnv.getConfig();
    String value = null;

    boolean isInsertSql = false;

    boolean isSelectSql = false;
    try {
      for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {

        value = sqlCommandCall.getOperands()[0];

        switch (sqlCommandCall.getSqlCommand()) {
          //配置
          case SET:
            String key = sqlCommandCall.getOperands()[0];
            String val = sqlCommandCall.getOperands()[1];
            if (val.contains(SystemConstant.LINE_FEED)) {
              throw new RuntimeException("set 语法值异常：" + val);
            }
            if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(key.trim())
                && SqlDialect.HIVE.name().equalsIgnoreCase(val.trim())) {
              config.setSqlDialect(SqlDialect.HIVE);
            } else {
              config.setSqlDialect(SqlDialect.DEFAULT);
            }

            break;
          case BEGIN_STATEMENT_SET:
          case END:
            break;
          //其他
          default:
            if (SqlCommand.INSERT_INTO.equals(sqlCommandCall.getSqlCommand())
                || SqlCommand.INSERT_OVERWRITE.equals(sqlCommandCall.getSqlCommand())) {
              isInsertSql = true;
            }
            if (SqlCommand.SELECT.equals(sqlCommandCall.getSqlCommand())) {
              isSelectSql = true;
            }
            CalciteParser parser = new CalciteParser(getSqlParserConfig(config));
            parser.parse(sqlCommandCall.getOperands()[0]);
            break;
        }
      }
    } catch (Exception e) {
      log.warn("语法异常：  sql={}  原因是: {}", value, e);
      throw new RuntimeException("语法异常   sql=" + value + "  原因:   " + e.getMessage());
    }
    if (!isInsertSql) {
      throw new RuntimeException(ValidationConstants.MESSAGE_010);
    }

    if (isSelectSql) {
      throw new RuntimeException(ValidationConstants.MESSAGE_011);
    }

  }

  private static SqlParser.Config getSqlParserConfig(TableConfig tableConfig) {
    return JavaScalaConversionUtil.toJava(getCalciteConfig(tableConfig).getSqlParserConfig())
        .orElseGet(
            () -> {
              SqlConformance conformance = getSqlConformance(tableConfig.getSqlDialect());
              return SqlParser
                  .config()
                  .withParserFactory(FlinkSqlParserFactories.create(conformance))
                  .withConformance(conformance)
                  .withLex(Lex.JAVA)
                  .withIdentifierMaxLength(256);
            }
        );
  }

  private static CalciteConfig getCalciteConfig(TableConfig tableConfig) {
    return TableConfigUtils.getCalciteConfig(tableConfig);
  }

  private static FlinkSqlConformance getSqlConformance(SqlDialect sqlDialect) {
    switch (sqlDialect) {
      case HIVE:
        return FlinkSqlConformance.HIVE;
      case DEFAULT:
        return FlinkSqlConformance.DEFAULT;
      default:
        throw new TableException("Unsupported SQL dialect: " + sqlDialect);
    }
  }

  /**
   * 字符串转sql
   */
  public static List<String> toSqlList(String sql) {
    if (StringUtils.isEmpty(sql)) {
      return Collections.emptyList();
    }
    return Arrays.asList(sql.split(SystemConstant.LINE_FEED));
  }


  /**
   * 设置方言
   *
   * @Param:[sql, tableConfig]
   * @return: java.lang.Boolean
   * @Author: zhuhuipei
   * @date 2022/6/24
   */
  private static Boolean setSqlDialect(String sql, TableConfig tableConfig) {
    final Matcher matcher = SqlCommand.SET.getPattern().matcher(sql);
    if (matcher.matches()) {
      final String[] groups = new String[matcher.groupCount()];
      for (int i = 0; i < groups.length; i++) {
        groups[i] = matcher.group(i + 1);
      }
      String key = groups[1].replace(ValidationConstants.SPLIT_1, ValidationConstants.SPACE).trim();
      String val = groups[2];
      if (ValidationConstants.TABLE_SQL_DIALECT_1.equalsIgnoreCase(key)) {
        if (SqlDialect.HIVE.name().equalsIgnoreCase(
            val.replace(ValidationConstants.SPLIT_1, ValidationConstants.SPACE).trim())) {
          tableConfig.setSqlDialect(SqlDialect.HIVE);
        } else {
          tableConfig.setSqlDialect(SqlDialect.DEFAULT);
        }
      } else {
        Configuration configuration = tableConfig.getConfiguration();
        configuration.setString(key, val);
      }
      return true;
    }
    return false;
  }


}
