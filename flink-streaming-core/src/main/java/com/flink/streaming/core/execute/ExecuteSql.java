package com.flink.streaming.core.execute;

import com.flink.streaming.common.model.SqlCommandCall;
import com.flink.streaming.core.config.Configurations;
import com.flink.streaming.core.logs.LogPrint;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobID;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.CatalogSinkModifyOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.command.SetOperation;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/21
 * @time 17:29
 */
@Slf4j
public class ExecuteSql {


  public static JobID exeSql(List<String> sqlList, TableEnvironment tEnv) {
    Parser parser = ((TableEnvironmentInternal) tEnv).getParser();

    List<ModifyOperation> modifyOperationList=new ArrayList<>();

    for (String stmt : sqlList) {
      Operation operation = parser.parse(stmt).get(0);
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
          Configurations.setSingleConfiguration(tEnv, setOperation.getKey().get(),
              setOperation.getValue().get());
          break;

        case "BeginStatementSetOperation":
          System.out.println("####stmt= "+stmt);
          log.info("####stmt={}",stmt);
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
         ((TableEnvironmentInternal) tEnv).executeInternal(parser.parse(stmt).get(0));
          break;
        case "CatalogSinkModifyOperation":
          modifyOperationList.add((CatalogSinkModifyOperation)operation);
          break;
        default:
          throw new RuntimeException("不支持该语法 sql=" + stmt);
      }
    }
    TableResult tableResult=((TableEnvironmentInternal) tEnv).executeInternal(modifyOperationList);
    if (tableResult.getJobClient().orElse(null)!=null){
      return tableResult.getJobClient().get().getJobID();
    }
    throw new RuntimeException("任务运行失败 没有获取到JobID");

  }


  /**
   * 执行sql
   *
   * @author zhuhuipei
   * @date 2021/3/21
   * @time 17:33
   */
  public static void exeSql(List<SqlCommandCall> sqlCommandCallList, TableEnvironment tEnv,
      StatementSet statementSet) {
    for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {
      switch (sqlCommandCall.sqlCommand) {
        //配置
        case SET:
          Configurations.setSingleConfiguration(tEnv, sqlCommandCall.operands[0],
              sqlCommandCall.operands[1]);
          break;
        //insert 语句
        case INSERT_INTO:
        case INSERT_OVERWRITE:
          LogPrint.logPrint(sqlCommandCall);
          statementSet.addInsertSql(sqlCommandCall.operands[0]);
          break;
        //显示语句
        case SELECT:
        case SHOW_CATALOGS:
        case SHOW_DATABASES:
        case SHOW_MODULES:
        case SHOW_TABLES:
          LogPrint.queryRestPrint(tEnv, sqlCommandCall);
          break;
        // 兼容sql-client.sh的用法，只显示但不执行
        case BEGIN_STATEMENT_SET:
        case END:
          LogPrint.logPrint(sqlCommandCall);
          break;
        default:
          LogPrint.logPrint(sqlCommandCall);
          tEnv.executeSql(sqlCommandCall.operands[0]);
          break;
      }
    }
  }
}
