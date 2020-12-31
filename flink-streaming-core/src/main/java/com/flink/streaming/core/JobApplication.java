package com.flink.streaming.core;


import com.flink.streaming.core.model.CheckPointParam;
import com.flink.streaming.core.model.JobRunParam;
import com.flink.streaming.core.model.SqlConfig;
import com.flink.streaming.core.sql.SqlParser;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.calcite.shaded.com.google.common.base.Preconditions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            setUdf(tEnv, sqlConfig);

            //设置checkPoint
            setCheckpoint(env, jobRunParam.getCheckPointParam());

            //设置tableConfig 用户可以通过 table.local-time-zone 自行设置
            TableConfig tableConfig = tEnv.getConfig();
            tableConfig.setLocalTimeZone(ZoneId.of("Asia/Shanghai"));

            //加载配置
            setConfiguration(tEnv, sqlConfig);

            //执行ddl
            callDdl(tEnv, sqlConfig);

            //执行view
            callView(tEnv, sqlConfig);

            //执行dml
            callDml(tEnv, sqlConfig);


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("任务执行失败:" + e.getMessage());
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
            log.info("#############setConfiguration#############\n  {} {}", entry.getKey(), entry.getValue());
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
            tEnv.executeSql(ddl);
        }
    }


    private static void callView(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getViewList()== null) {
            return;
        }

        for (String view : sqlConfig.getViewList()) {
            System.out.println("#############view############# \n" + view);
            log.info("#############view############# \n {}", view);
            tEnv.executeSql(view);
        }
    }


    private static void setUdf(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getUdfList() == null) {
            return;
        }

        for (String udf : sqlConfig.getUdfList()) {
            System.out.println("#############udf############# \n" + udf);
            log.info("#############udf############# \n {}", udf);
            tEnv.executeSql(udf);
        }
    }


    private static void callDml(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || sqlConfig.getDmlList() == null) {
            return;
        }
        for (String dml : sqlConfig.getDmlList()) {
            System.out.println("#############dml############# \n" + dml);
            log.info("#############dml############# \n {}", dml);
            tEnv.executeSql(dml);
        }
    }

    private static JobRunParam buildParam(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String sqlPath = parameterTool.get("sql");
        Preconditions.checkNotNull(sqlPath, "-sql参数 不能为空");
        JobRunParam jobRunParam = new JobRunParam();
        jobRunParam.setSqlPath(sqlPath);
        jobRunParam.setCheckPointParam(buildCheckPointParam(parameterTool));
        return jobRunParam;
    }


    /**
     * 构建checkPoint参数
     *
     * @author zhuhuipei
     * @date 2020-08-23
     * @time 22:44
     */
    private static CheckPointParam buildCheckPointParam(ParameterTool parameterTool) throws Exception {

        String checkpointDir = parameterTool.get("checkpointDir");
        //如果checkpointDir为空不启用CheckPoint
        if (StringUtils.isEmpty(checkpointDir)) {
            return null;
        }
        String checkpointingMode = parameterTool.get("checkpointingMode",
                CheckpointingMode.EXACTLY_ONCE.name());

        String checkpointInterval = parameterTool.get("checkpointInterval");

        String checkpointTimeout = parameterTool.get("checkpointTimeout");

        String tolerableCheckpointFailureNumber = parameterTool.get("tolerableCheckpointFailureNumber");

        String asynchronousSnapshots = parameterTool.get("asynchronousSnapshots");

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

        log.info("开启checkpoint checkPointParam={}", checkPointParam);

        // 默认每60s保存一次checkpoint
        env.enableCheckpointing(checkPointParam.getCheckpointInterval());

        CheckpointConfig checkpointConfig = env.getCheckpointConfig();

        //开始一致性模式是：精确一次 exactly-once
        if (StringUtils.isEmpty(checkPointParam.getCheckpointingMode()) ||
                CheckpointingMode.EXACTLY_ONCE.name().equalsIgnoreCase(checkPointParam.getCheckpointingMode())) {
            checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        } else {
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
            env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir(),
                    checkPointParam.getAsynchronousSnapshots()));
        } else {
            env.setStateBackend(new FsStateBackend(checkPointParam.getCheckpointDir()));
        }

    }


}
