package com.flink.streaming.core;


import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.ExecMode;
import com.flink.streaming.common.model.SqlCommandCall;
import com.flink.streaming.common.sql.SqlFileParser;
import com.flink.streaming.core.checkpoint.CheckPointParams;
import com.flink.streaming.core.checkpoint.FsCheckPoint;
import com.flink.streaming.core.execute.ExecuteSql;
import com.flink.streaming.core.model.JobRunParam;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.calcite.shaded.com.google.common.base.Preconditions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
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

    public static void main(String[] args) {

        try {
            Arrays.stream(args).forEach(arg -> log.info("{}", arg));

            JobRunParam jobRunParam = buildParam(args);

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            EnvironmentSettings settings = null;
            if (jobRunParam.getExecMode() != null && ExecMode.BATCH.equals(jobRunParam.getExecMode())) {
                //批处理
                settings = EnvironmentSettings.newInstance()
                        .useBlinkPlanner()
                        .inBatchMode()
                        .build();
            } else {
                //默认是流 流处理
                settings = EnvironmentSettings.newInstance()
                        .useBlinkPlanner()
                        .inStreamingMode()
                        .build();
            }


            TableEnvironment tEnv = StreamTableEnvironment.create(env, settings);

            //设置checkPoint
            FsCheckPoint.setCheckpoint(env, jobRunParam.getCheckPointParam());

            List<String> sql = Files.readAllLines(Paths.get(jobRunParam.getSqlPath()));

            List<SqlCommandCall> sqlCommandCallList = SqlFileParser.fileToSql(sql);

            StatementSet statementSet = tEnv.createStatementSet();

            ExecuteSql.exeSql(sqlCommandCallList, tEnv, statementSet);


            TableResult tableResult = statementSet.execute();
            if (tableResult == null || tableResult.getJobClient().get() == null ||
                    tableResult.getJobClient().get().getJobID() == null) {
                throw new RuntimeException("任务运行失败 没有获取到JobID");
            }
            JobID jobID = tableResult.getJobClient().get().getJobID();

            System.out.println(SystemConstant.QUERY_JOBID_KEY_WORD + jobID);

            log.info(SystemConstant.QUERY_JOBID_KEY_WORD + "{}", jobID);

        } catch (Exception e) {
            System.err.println("任务执行失败:" + e.getMessage());
            log.error("任务执行失败：", e);
        }


    }


    private static JobRunParam buildParam(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        String sqlPath = parameterTool.get("sql");
        Preconditions.checkNotNull(sqlPath, "-sql参数 不能为空");
        JobRunParam jobRunParam = new JobRunParam();
        jobRunParam.setSqlPath(sqlPath);
        jobRunParam.setCheckPointParam(CheckPointParams.buildCheckPointParam(parameterTool));
        return jobRunParam;
    }

}
