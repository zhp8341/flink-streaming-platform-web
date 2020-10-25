package com.flink.streaming.core;


import com.flink.streaming.core.model.CheckPointParam;
import com.flink.streaming.core.model.JobRunParam;
import com.flink.streaming.core.model.SqlConfig;
import com.flink.streaming.core.sql.SqlParser;
import com.flink.streaming.core.udf.UdfFunctionManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.calcite.shaded.com.google.common.base.Preconditions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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


            env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

            EnvironmentSettings settings = EnvironmentSettings.newInstance()
                    .useBlinkPlanner()
                    .inStreamingMode()
                    .build();

            TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);


            List<String> sql = Files.readAllLines(Paths.get(jobRunParam.getSqlPath()));
            SqlConfig sqlConfig = SqlParser.parseToSqlConfig(sql);


            //注册自定义的udf
            UdfFunctionManager.registerTableUDF(tEnv, jobRunParam.getUdfJarPath(),sqlConfig.getUdfMap());


            //设置checkPoint
            setCheckpoint(env, jobRunParam.getCheckPointParam());

            //设置tableConfig
            TableConfig tableConfig = tEnv.getConfig();
            tableConfig.setLocalTimeZone(ZoneId.of("Asia/Shanghai"));



            //加载配置
            setConfiguration(tEnv, sqlConfig);

            //执行ddl
            callDdl(tEnv, sqlConfig);

            //执行dml
            callDml(tEnv, sqlConfig);

            tEnv.execute("JobApplication-" + UUID.randomUUID().toString());


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("任务执行失败:" + e);
            log.error("任务执行失败：", e);
        }


    }




    /**
     * 设置Configuration
     *
     * @author zhuhuipei
     * @date 2020-06-23
     * @time 00:46
     */
    private static void setConfiguration(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || MapUtils.isEmpty(sqlConfig.getMapConfig())) {
            return;
        }
        Configuration configuration = tEnv.getConfig().getConfiguration();
        for (Map.Entry<String, String> entry : sqlConfig.getMapConfig().entrySet()) {
            log.info("#############setConfiguration#############\n  {} {}", entry.getKey(),entry.getValue());
            configuration.setString(entry.getKey(), entry.getValue());
        }
    }


    private static void callDdl(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getDdlList() == null) {
            return;
        }
        for (String ddl : sqlConfig.getDdlList()) {
            System.out.println("#############ddl############# \n" + ddl);
            log.info("#############ddl############# \n {}", ddl);
            tEnv.sqlUpdate(ddl);
        }
    }

    private static void callDml(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getDmlList() == null) {
            return;
        }
        for (String dml : sqlConfig.getDmlList()) {
            System.out.println("#############dml############# \n" + dml);
            log.info("#############dml############# \n {}", dml);
            tEnv.sqlUpdate(dml);
        }
    }

    private static JobRunParam buildParam(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("sql", true, "sql");
        options.addOption("checkpointInterval", true, "checkpointInterval");
        options.addOption("checkpointingMode", true, "checkpointingMode");
        options.addOption("checkpointTimeout", true, "checkpointTimeout");
        options.addOption("checkpointDir", true, "checkpointDir");
        options.addOption("tolerableCheckpointFailureNumber", true, "tolerableCheckpointFailureNumber");
        options.addOption("asynchronousSnapshots", true, "asynchronousSnapshots");
        options.addOption("udfJarPath", true, "udfJarPath");
        CommandLineParser parser = new DefaultParser();
        CommandLine cl = parser.parse(options, args);
        String sqlPath = cl.getOptionValue("sql");
        String udfJarPath = cl.getOptionValue("udfJarPath");
        Preconditions.checkNotNull(sqlPath, "sql 目录为空");

        JobRunParam jobRunParam = new JobRunParam();
        jobRunParam.setSqlPath(sqlPath);
        jobRunParam.setCheckPointParam(buildCheckPointParam(cl));
        jobRunParam.setUdfJarPath(udfJarPath);

        return jobRunParam;
    }


    /**
     * 构建checkPoint参数
     *
     * @author zhuhuipei
     * @date 2020-08-23
     * @time 22:44
     */
    private static CheckPointParam buildCheckPointParam(CommandLine cl) throws Exception {

        String checkpointDir = cl.getOptionValue("checkpointDir");
        //如果checkpointDir为空不启用CheckPoint
        if (StringUtils.isEmpty(checkpointDir)) {
            return null;
        }
        String checkpointingMode = cl.getOptionValue("checkpointingMode", CheckpointingMode.EXACTLY_ONCE.name());

        String checkpointInterval = cl.getOptionValue("checkpointInterval");

        String checkpointTimeout = cl.getOptionValue("checkpointTimeout");

        String tolerableCheckpointFailureNumber = cl.getOptionValue("tolerableCheckpointFailureNumber");

        String asynchronousSnapshots = cl.getOptionValue("asynchronousSnapshots");

        CheckPointParam checkPointParam = new CheckPointParam();
        if (StringUtils.isNotEmpty(asynchronousSnapshots)) {
            checkPointParam.setAsynchronousSnapshots(Boolean.getBoolean(asynchronousSnapshots));
        }
        checkPointParam.setCheckpointDir(checkpointDir);

        checkPointParam.setCheckpointingMode(checkpointingMode);
        if (StringUtils.isNotEmpty(checkpointInterval)) {
            checkPointParam.setCheckpointInterval(Long.valueOf(checkpointInterval));
        }
        if (StringUtils.isNotEmpty(checkpointTimeout)) {
            checkPointParam.setCheckpointTimeout(Long.valueOf(checkpointTimeout));
        }
        if (StringUtils.isNotEmpty(tolerableCheckpointFailureNumber)) {
            checkPointParam.setTolerableCheckpointFailureNumber(Integer.valueOf(tolerableCheckpointFailureNumber));
        }
        return checkPointParam;

    }


    private static void setCheckpoint(StreamExecutionEnvironment env, CheckPointParam checkPointParam) {
        if (checkPointParam == null) {
            log.warn("############没有启用Checkpoint############");
            return;
        }
        if (StringUtils.isEmpty(checkPointParam.getCheckpointDir())) {
            throw new RuntimeException("checkpoint目录不存在");
        }

        // 默认每60s保存一次checkpoint
        env.enableCheckpointing(checkPointParam.getCheckpointInterval());

        CheckpointConfig checkpointConfig = env.getCheckpointConfig();

        //开始一致性模式是：精确一次 exactly-once
        if (StringUtils.isEmpty(checkPointParam.getCheckpointingMode())||
                CheckpointingMode.EXACTLY_ONCE.name().equalsIgnoreCase(checkPointParam.getCheckpointingMode())){
            checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        }else{
            checkpointConfig.setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        }

        //默认超时10 minutes.
        checkpointConfig.setCheckpointTimeout(checkPointParam.getCheckpointTimeout());
        //确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
        checkpointConfig.setMinPauseBetweenCheckpoints(500);
        //同一时间只允许进行一个检查点
        checkpointConfig.setMaxConcurrentCheckpoints(2);

        checkpointConfig.setTolerableCheckpointFailureNumber(checkPointParam.getTolerableCheckpointFailureNumber());

        if (checkPointParam.getAsynchronousSnapshots() != null) {
            env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir(), checkPointParam.getAsynchronousSnapshots()));
        } else {
            env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir()));
        }

    }


}
