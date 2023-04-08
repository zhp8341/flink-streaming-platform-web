import com.flink.streaming.common.sql.SqlFileParser;
import com.flink.streaming.core.execute.ExecuteSql;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class Demo {


  private static String test_sql_file = "/Users/edy/git/flink-streaming-platform-web/flink-streaming-core/src/test.sql";


  public static void main(String[] args) throws Exception {

    EnvironmentSettings settings = null;

    TableEnvironment tEnv = null;

    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    settings = EnvironmentSettings.newInstance()
        .inStreamingMode()
        .build();
    tEnv = StreamTableEnvironment.create(env, settings);

    List<String> fileList = Files.readAllLines(Paths.get(test_sql_file));

//    List<SqlCommand>  res=SqlFileParser.fileToSqlCommand(fileList,tEnv);
//    System.out.println(res);

    List<String> sqlList = SqlFileParser.parserSql(fileList);
    System.out.println(sqlList);

    ExecuteSql.exeSql(sqlList,tEnv);



  }
}
