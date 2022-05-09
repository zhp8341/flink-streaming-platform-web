package com.flink.streaming.sql.validation;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.SqlCommand;
import com.flink.streaming.common.model.SqlCommandCall;
import com.flink.streaming.common.sql.SqlFileParser;
import com.flink.streaming.sql.util.ValidationConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.config.Lex;
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
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.command.SetOperation;
import org.apache.flink.table.planner.calcite.CalciteConfig;
import org.apache.flink.table.planner.delegation.FlinkSqlParserFactories;
import org.apache.flink.table.planner.parse.CalciteParser;
import org.apache.flink.table.planner.utils.JavaScalaConversionUtil;
import org.apache.flink.table.planner.utils.TableConfigUtils;


@Slf4j
public class SqlValidation {


    public static void explainStmt(List<String> stmtList) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
            .useBlinkPlanner()
            .inStreamingMode()
            .build();

        TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

        List<Operation> modifyOperationList=new ArrayList<>();
        Parser parser = ((TableEnvironmentInternal) tEnv).getParser();
        Operation operation=null;
        String  explainStmt=null;
        try {
            for (String stmt : stmtList) {
                explainStmt=stmt;
                operation= parser.parse(stmt).get(0);
                log.info("operation={}", operation.getClass().getSimpleName());
                switch (operation.getClass().getSimpleName()) {
                    //显示
                    case "ShowTablesOperation":
                    case "ShowCatalogsOperation":
                    case "ShowCreateTableOperation":
                    case "ShowCurrentCatalogOperation":
                    case "ShowCurrentDatabaseOperation":
                    case "ShowDatabasesOperation":
                    case "ShowFunctionsOperation":
                    case "ShowModulesOperation":
                    case "ShowPartitionsOperation":
                    case "ShowViewsOperation":
                    case "ExplainOperation":
                    case "DescribeTableOperation":
                        tEnv.executeSql(stmt).print();
                        break;
                    //set
                    case "SetOperation":
                        SetOperation setOperation = (SetOperation) operation;
                        String key = setOperation.getKey().get();
                        String value = setOperation.getValue().get();
                        Configuration configuration = tEnv.getConfig().getConfiguration();
                        log.info("#############setConfiguration#############\n  key={} value={}",
                            key, value);
                        configuration.setString(key, value);
                        break;

                    case "BeginStatementSetOperation":
                        System.out.println("####stmt= " + stmt);
                        log.info("####stmt={}", stmt);
                        break;
                    case "DropTableOperation":
                    case "DropCatalogFunctionOperation":
                    case "DropTempSystemFunctionOperation":
                    case "DropCatalogOperation":
                    case "DropDatabaseOperation":
                    case "DropViewOperation":
                    case "CreateTableOperation":
                    case "CreateViewOperation":
                    case "CreateDatabaseOperation":
                    case "CreateCatalogOperation":
                    case "CreateTableASOperation":
                    case "CreateCatalogFunctionOperation":
                    case "CreateTempSystemFunctionOperation":
                    case "AlterTableOperation":
                    case "AlterViewOperation":
                    case "AlterDatabaseOperation":
                    case "AlterCatalogFunctionOperation":
                    case "UseCatalogOperation":
                    case "UseDatabaseOperation":
                    case "LoadModuleOperation":
                    case "UnloadModuleOperation":
                    case "NopOperation":
                            ((TableEnvironmentInternal) tEnv)
                            .executeInternal(parser.parse(stmt).get(0));
                        break;
                    case "CatalogSinkModifyOperation":
                        modifyOperationList.add(operation);
                        break;
                    default:
                        throw new RuntimeException("不支持该语法 sql=" + stmt);
                }
            }
            if (modifyOperationList.size() > 0) {
                ((TableEnvironmentInternal) tEnv).explainInternal(modifyOperationList);
            }
        }catch (Exception e) {
            log.error("语法异常：  sql={}  原因是: ", explainStmt, e);
            throw new RuntimeException("语法异常   sql=" + explainStmt + "  原因:   " + e.getMessage());
        }


    }


    /**
     * @author zhuhuipei
     * @date 2021/3/27
     * @time 10:10
     *
     */
    @Deprecated
    public static void preCheckSql(List<String> sql) {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .useBlinkPlanner()
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

                value = sqlCommandCall.operands[0];

                switch (sqlCommandCall.sqlCommand) {
                    //配置
                    case SET:
                        String key = sqlCommandCall.operands[0];
                        String val = sqlCommandCall.operands[1];
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
                        if (SqlCommand.INSERT_INTO.equals(sqlCommandCall.sqlCommand)
                                || SqlCommand.INSERT_OVERWRITE.equals(sqlCommandCall.sqlCommand)) {
                            isInsertSql = true;
                        }
                        if (SqlCommand.SELECT.equals(sqlCommandCall.sqlCommand)) {
                            isSelectSql = true;
                        }
                        CalciteParser parser = new CalciteParser(getSqlParserConfig(config));
                        parser.parse(sqlCommandCall.operands[0]);
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
     */
    public static List<String> toSqlList(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return Collections.emptyList();
        }
        return Arrays.asList(sql.split(SystemConstant.LINE_FEED));
    }


}
