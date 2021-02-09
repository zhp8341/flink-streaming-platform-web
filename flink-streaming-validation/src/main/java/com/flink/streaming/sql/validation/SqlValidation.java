package com.flink.streaming.sql.validation;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.model.SqlConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.calcite.shaded.com.google.common.collect.Lists;
import org.apache.flink.sql.parser.validate.FlinkSqlConformance;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.*;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.planner.calcite.CalciteConfig;
import org.apache.flink.table.planner.calcite.CalciteParser;
import org.apache.flink.table.planner.delegation.FlinkSqlParserFactories;
import org.apache.flink.table.planner.utils.JavaScalaConversionUtil;
import org.apache.flink.table.planner.utils.TableConfigUtils;

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


    //TODO 目前只能通过flink的CalciteParser校验，暂时没有找到好的解决方案
    /**
     * sql校验
     *
     * @author zhuhuipei
     * @date 2021/1/21
     * @time 22:24
     */
    public static void checkSql(List<String> sqlList) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
                .inStreamingMode()
                .build();

        TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);
        CalciteParser parser = new CalciteParser(getSqlParserConfig(tEnv.getConfig()));
        for (String sql : sqlList) {
            log.info("###################开始校验sql############");
            log.info("sql={}", sql);
            try {
                parser.parse(sql);
            } catch (Exception e) {
                log.error("校验sql失败 sql={}", sql, e);
                throw e;
            }
            log.info("###################校验通过############");
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

        SqlConfig sqlConfig = com.flink.streaming.common.sql.SqlParser.
                parseToSqlConfig(Arrays.asList(sql.split(SystemConstant.LINE_FEED)));
        if (sqlConfig == null) {
            log.warn("解析 sqlConfig 为空 sql={} ", sql);
            return Collections.emptyList();
        }

        List<String> listSql = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(sqlConfig.getDdlList())) {
            listSql.addAll(sqlConfig.getDdlList());
        }
        if (CollectionUtils.isNotEmpty(sqlConfig.getDmlList())) {
            listSql.addAll(sqlConfig.getDmlList());
        }
        if (CollectionUtils.isNotEmpty(sqlConfig.getViewList())) {
            listSql.addAll(sqlConfig.getViewList());
        }
        return listSql;
    }


    private static SqlParser.Config getSqlParserConfig(TableConfig tableConfig) {
        return JavaScalaConversionUtil.<SqlParser.Config>toJava(getCalciteConfig(tableConfig).getSqlParserConfig()).orElseGet(
                () -> {
                    SqlConformance conformance = getSqlConformance(tableConfig);
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

    private static FlinkSqlConformance getSqlConformance(TableConfig tableConfig) {
        SqlDialect sqlDialect = tableConfig.getSqlDialect();
        switch (sqlDialect) {
            case HIVE:
                return FlinkSqlConformance.HIVE;
            case DEFAULT:
                return FlinkSqlConformance.DEFAULT;
            default:
                throw new TableException("Unsupported SQL dialect: " + sqlDialect);
        }
    }

}
