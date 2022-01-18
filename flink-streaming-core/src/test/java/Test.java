import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.flink.streaming.common.model.SqlCommandCall;
import com.flink.streaming.common.sql.SqlFileParser;
import com.flink.streaming.core.logs.LogPrint;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 21:36
 */
public class Test {

    public static void main(String[] args) throws IOException {
        System.out.println(File.separator);

        ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println(threadClassLoader);

        List<String> sql = Files.readAllLines(Paths.get("D:\\ideaprojects\\flink-test\\src\\main\\resources\\online.sql"));
        List<SqlCommandCall> sqlCommandCallList = SqlFileParser.fileToSql(sql);
        for (SqlCommandCall sqlCommandCall : sqlCommandCallList) {
            LogPrint.logPrint(sqlCommandCall);
        }
    }
}
