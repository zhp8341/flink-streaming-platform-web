package com.flink.streaming.core.execute;

import com.flink.streaming.common.model.SqlConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.api.TableEnvironment;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 10:59
 */
@Slf4j
public class Executes {

    public static void callDdl(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getDdlList() == null) {
            return;
        }

        for (String ddl : sqlConfig.getDdlList()) {
            System.out.println("#############ddl############# \n" + ddl);
            log.info("#############ddl############# \n {}", ddl);
            tEnv.executeSql(ddl);
        }
    }


    public static void callView(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getViewList()== null) {
            return;
        }

        for (String view : sqlConfig.getViewList()) {
            System.out.println("#############view############# \n" + view);
            log.info("#############view############# \n {}", view);
            tEnv.executeSql(view);
        }
    }


    public static void setUdf(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getUdfList() == null) {
            return;
        }

        for (String udf : sqlConfig.getUdfList()) {
            System.out.println("#############udf############# \n" + udf);
            log.info("#############udf############# \n {}", udf);
            tEnv.executeSql(udf);
        }
    }


    public static void callDml(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getDmlList() == null) {
            return;
        }
        for (String dml : sqlConfig.getDmlList()) {
            System.out.println("#############dml############# \n" + dml);
            log.info("#############dml############# \n {}", dml);
            tEnv.executeSql(dml);
        }
    }
}
