package com.flink.streaming.sql.validation;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.model.SqlCommandCall;
import com.flink.streaming.common.sql.SqlFileParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.SqlDialect;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.config.TableConfigOptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 10:51
 */
@Slf4j
public class SqlValidation {


    //TODO 暂时没有找到好的解决方案

    /**
     * sql校验
     *
     * @author zhuhuipei
     * @date 2021/1/21
     * @time 22:24
     */
    public static void preCheckSql(List<String> sql) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        List<SqlCommandCall> sqlCommandCallList = SqlFileParser.fileToSql(sql);

        StatementSet statementSet = tEnv.createStatementSet();

        String value = null;

        try {
            for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {

                value = sqlCommandCall.operands[0];

                switch (sqlCommandCall.sqlCommand) {
                    case USE_CATALOG:
                    case CREATE_CATALOG:
                        throw new RuntimeException("暂时不支持 CATALOG 相关语法校验(请不要点击 sql预校验 按钮了)");
                        //配置
                    case SET:
                        String key = sqlCommandCall.operands[0];
                        String val = sqlCommandCall.operands[1];

                        if (TableConfigOptions.TABLE_SQL_DIALECT.key().equalsIgnoreCase(key.trim())
                                 && SqlDialect.HIVE.name().equalsIgnoreCase(val.trim())) {
                            throw new RuntimeException("暂时不支持 Hive相关语法校验 (请不要点击 sql预校验 按钮)");
                        }
                        Configuration configuration = tEnv.getConfig().getConfiguration();
                        configuration.setString(key, val);
                        break;
                    //insert 语句
                    case INSERT_INTO:
                    case INSERT_OVERWRITE:
                        statementSet.addInsertSql(sqlCommandCall.operands[0]);
                        break;
                    //其他
                    default:
                        tEnv.executeSql(sqlCommandCall.operands[0]);
                        break;
                }
            }
        } catch (Exception e) {
            log.warn("语法异常：{}  原因 {}", value, e);
            throw new RuntimeException("语法异常  " + value + "  原因  " + e.getMessage());
        }

    }


    /**
     * 字符串转sql
     *
     * @author zhuhuipei
     * @date 2021/1/22
     * @time 22:45
     */
    public static List<String> toSqlList(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return Collections.emptyList();
        }
        return Arrays.asList(sql.split(SystemConstant.LINE_FEED));
    }


}
