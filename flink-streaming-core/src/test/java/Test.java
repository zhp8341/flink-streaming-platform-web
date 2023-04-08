import com.flink.streaming.core.config.Configurations;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.command.SetOperation;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 21:36
 */
public class Test {

  private static List<String> sqlList = new ArrayList<>();

  static {
    sqlList.add("SET 'table.local-time-zone' = 'Asia/Shanghai' ");

    sqlList.add("  CREATE TABLE source_table (\n"
        + "  f0 INT,\n"
        + "  f1 INT,\n"
        + "  f2 STRING\n"
        + " ) WITH (\n"
        + "  'connector' = 'datagen',\n"
        + "  'rows-per-second'='5'\n"
        + " )");
    sqlList.add(" CREATE TABLE print_table (\n"
        + "  f0 INT,\n"
        + "  f1 INT,\n"
        + "  f2 STRING\n"
        + " ) WITH (\n"
        + "  'connector' = 'print'\n"
        + " )");
    sqlList.add("show tables");
    sqlList.add("insert into print_table select f0,f1,f2 from source_table");
  }


  static EnvironmentSettings settings = null;

  static TableEnvironment tEnv = null;

  public static void main(String[] args) throws IOException {
//        System.out.println(File.separator);
//
//        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
//        System.out.println(threadClassLoader);
//
//        List<String> sql = Files.readAllLines(Paths.get("D:\\ideaprojects\\flink-test\\src\\main\\resources\\online.sql"));
//        List<SqlCommandCall> sqlCommandCallList = SqlFileParser.fileToSql(sql);
//        for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {
//            LogPrint.logPrint(sqlCommandCall);
//        }

//        Parser parser=new ParserImpl();

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    settings = EnvironmentSettings.newInstance()
        .inStreamingMode()
        .build();
    tEnv = StreamTableEnvironment.create(env, settings);

    Parser parser = ((TableEnvironmentInternal) tEnv).getParser();
    for (String sql : sqlList) {

      Operation operation = parser.parse(sql).get(0);
      System.out.println(operation.getClass());
      switch (operation.getClass().getSimpleName()) {

        case "ShowTablesOperation":
          tEnv.executeSql(sql).print();
          break;
        case "SetOperation":

          SetOperation setOperation = (SetOperation) operation;
          Configurations.setSingleConfiguration(tEnv, setOperation.getKey().get(),
              setOperation.getValue().get());
          ((TableEnvironmentInternal) tEnv).executeInternal(parser.parse(sql).get(0));
          break;
        default:
          ((TableEnvironmentInternal) tEnv).executeInternal(parser.parse(sql).get(0));
      }

//      if (operation instanceof ShowTablesOperation){
//
//      }else{
//
//      }

    }

//    TableResult tableResult = statementSet.execute();
//
//    JobID jobID = tableResult.getJobClient().get().getJobID();
//
//    System.out.println(SystemConstant.QUERY_JOBID_KEY_WORD + jobID);

//    List<Operation> list =new ArrayList<>();
//
//    System.out.println(list);
//    for (Operation operation : list) {
//      ((TableEnvironmentInternal) tEnv).executeInternal(operation);
//    }

  }
}
