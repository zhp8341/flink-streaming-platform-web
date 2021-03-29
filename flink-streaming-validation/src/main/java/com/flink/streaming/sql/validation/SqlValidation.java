package com.flink.streaming.sql.validation;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.model.SqlCommandCall;
import com.flink.streaming.common.sql.SqlFileParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.sql.parser.validate.FlinkSqlConformance;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.config.TableConfigOptions;
import org.apache.flink.table.planner.calcite.CalciteConfig;
import org.apache.flink.table.planner.calcite.CalciteParser;
import org.apache.flink.table.planner.delegation.FlinkSqlParserFactories;
import org.apache.flink.table.planner.utils.JavaScalaConversionUtil;
import org.apache.flink.table.planner.utils.TableConfigUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Slf4j
public class SqlValidation {

    //TODO 暂时没有找到好的解决方案
    /**
     *
     * @author zhuhuipei
     * @date 2021/3/27
     * @time 10:10
     */
    public static void preCheckSql(List<String> sql) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        List<SqlCommandCall> sqlCommandCallList = SqlFileParser.fileToSql(sql);
        TableConfig config = tEnv.getConfig();
        String value = null;

        try {
            for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {

                value = sqlCommandCall.operands[0];

                switch (sqlCommandCall.sqlCommand) {
                    //配置
                    case SET:
                        String key = sqlCommandCall.operands[0];
                        String val = sqlCommandCall.operands[1];
                        if (val.contains(SystemConstant.LINE_FEED)){
                           throw new RuntimeException("set 语法值异常："+val) ;
                        }
                        if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(key.trim())
                                && SqlDialect.HIVE.name().equalsIgnoreCase(val.trim())) {
                            config.setSqlDialect(SqlDialect.HIVE);
                        }else{
                            config.setSqlDialect(SqlDialect.DEFAULT);
                        }

                        break;
                    //其他
                    default:
                        CalciteParser parser = new CalciteParser(getSqlParserConfig(config));
                        parser.parse(sqlCommandCall.operands[0]);
                        break;
                }
            }
        } catch (Exception e) {
            log.warn("语法异常：  sql={}  原因是: {}", value, e);
            throw new RuntimeException("语法异常   sql=" + value + "  原因:   " + e.getMessage());
        }

    }

    private static SqlParser.Config getSqlParserConfig(TableConfig tableConfig) {
        return JavaScalaConversionUtil.toJava(getCalciteConfig(tableConfig).getSqlParserConfig()).orElseGet(
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
     *
     */
    public static List<String> toSqlList(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return Collections.emptyList();
        }
        return Arrays.asList(sql.split(SystemConstant.LINE_FEED));
    }


}
