package com.flink.streaming.core;



import com.flink.streaming.common.model.SqlConfig;
import com.flink.streaming.common.sql.SqlParser;
import com.flink.streaming.core.checkpoint.CheckPointParams;
import com.flink.streaming.core.checkpoint.FsCheckPoint;
import com.flink.streaming.core.config.Configurations;
import com.flink.streaming.core.enums.CatalogType;
import com.flink.streaming.core.execute.Executes;
import com.flink.streaming.core.model.JobRunParam;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.calcite.shaded.com.google.common.base.Preconditions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.GenericInMemoryCatalog;
import org.apache.flink.table.catalog.hive.HiveCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 00:33
 */

public class JobApplication {

    private static final Logger log = LoggerFactory.getLogger(JobApplication.class);

    public static void main(String[] args) throws Exception {

        Arrays.stream(args).forEach(arg -> log.info("{}", arg));

        try {
            JobRunParam jobRunParam = buildParam(args);

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            EnvironmentSettings settings = EnvironmentSettings.newInstance()
                    .useBlinkPlanner()
                    .inStreamingMode()
                    .build();

            TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

            List<String> sql = Files.readAllLines(Paths.get(jobRunParam.getSqlPath()));

            SqlConfig sqlConfig = SqlParser.parseToSqlConfig(sql);

            //注册自定义的udf
            Executes.setUdf(tEnv, sqlConfig);

            //设置checkPoint
            FsCheckPoint.setCheckpoint(env, jobRunParam.getCheckPointParam());

            //设置tableConfig 用户可以通过 table.local-time-zone 自行设置
            TableConfig tableConfig = tEnv.getConfig();
            tableConfig.setLocalTimeZone(ZoneId.of("Asia/Shanghai"));

            //加载配置
            Configurations.setConfiguration(tEnv, sqlConfig);

            //配置catalog
            //TODO udf 不能生效
            //setCatalog(tEnv, jobRunParam);

            //执行ddl
            Executes.callDdl(tEnv, sqlConfig);

            //执行view
            Executes.callView(tEnv, sqlConfig);

            //执行dml
            Executes.callDml(tEnv, sqlConfig);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("任务执行失败:" + e.getMessage());
            log.error("任务执行失败：", e);
        }


    }


    /**
     * 设置Catalog
     *
     * @author Jim Chen
     * @date 2021-01-21
     * @time 01:18
     * @param tEnv
     * @param jobRunParam
     */
    private static void setCatalog(TableEnvironment tEnv, JobRunParam jobRunParam) {
        String catalogType = jobRunParam.getCatalog();
        String hiveConfDir = jobRunParam.getHiveConfDir();


        log.info("catalogType ={} hiveConfDir={}",catalogType,hiveConfDir);

        Catalog catalog = null;
        String catalogName = null;
        if (CatalogType.HIVE.toString().equalsIgnoreCase(catalogType)) {
            catalogName = "hive_catalog";
            catalog = new HiveCatalog(
                    catalogName,
                    "default",
                    hiveConfDir);
        } else if (CatalogType.JDBC.toString().equalsIgnoreCase(catalogType)) {

        } else if (CatalogType.POSTGRES.toString().equalsIgnoreCase(catalogType)) {

        } else {
            //  default catalog is memory
            catalogName = "memory_catalog";
            catalog = new GenericInMemoryCatalog(catalogName);
        }
        tEnv.registerCatalog(catalogName, catalog);
        tEnv.useCatalog(catalogName);
    }




    private static JobRunParam buildParam(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String sqlPath = parameterTool.get("sql");
        Preconditions.checkNotNull(sqlPath, "-sql参数 不能为空");
        String catalog = parameterTool.get("catalog", "memory");
        String hiveConfDir = parameterTool.get("hive_conf_dir");
        JobRunParam jobRunParam = new JobRunParam();
        jobRunParam.setSqlPath(sqlPath);
        jobRunParam.setCheckPointParam(CheckPointParams.buildCheckPointParam(parameterTool));
        jobRunParam.setCatalog(catalog);
        jobRunParam.setHiveConfDir(hiveConfDir);
        return jobRunParam;
    }

}
