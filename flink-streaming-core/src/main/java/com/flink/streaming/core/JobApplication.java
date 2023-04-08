package com.flink.streaming.core;


import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.JobTypeEnum;
import com.flink.streaming.common.sql.SqlFileParser;
import com.flink.streaming.common.utils.UrlUtils;
import com.flink.streaming.core.checkpoint.CheckPointParams;
import com.flink.streaming.core.checkpoint.FsCheckPoint;
import com.flink.streaming.core.execute.ExecuteSql;
import com.flink.streaming.core.model.JobRunParam;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 00:33
 */
public class JobApplication {

  private static final Logger LOG = LoggerFactory.getLogger(JobApplication.class);

  public static void main(String[] args) {

    try {
      Arrays.stream(args).forEach(arg -> LOG.info("{}", arg));

      JobRunParam jobRunParam = buildParam(args);

      List<String> fileList = null;

      if (UrlUtils.isHttpsOrHttp(jobRunParam.getSqlPath())) {
        fileList = UrlUtils.getSqlList(jobRunParam.getSqlPath());
      } else {
        fileList = Files.readAllLines(Paths.get(jobRunParam.getSqlPath()));
      }

      List<String> sqlList = SqlFileParser.parserSql(fileList);

      EnvironmentSettings settings = null;

      TableEnvironment tEnv = null;

      if (jobRunParam.getJobTypeEnum() != null
          && JobTypeEnum.SQL_BATCH.equals(jobRunParam.getJobTypeEnum())) {
        LOG.info("[SQL_BATCH]本次任务是批任务");
        //批处理
        settings = EnvironmentSettings.newInstance()
            .inBatchMode()
            .build();
        tEnv = TableEnvironment.create(settings);
      } else {
        LOG.info("[SQL_STREAMING]本次任务是流任务");
        //默认是流 流处理 目的是兼容之前版本
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        settings = EnvironmentSettings.newInstance()
            .inStreamingMode()
            .build();
        tEnv = StreamTableEnvironment.create(env, settings);
        //设置checkPoint
        FsCheckPoint.setCheckpoint(env, jobRunParam.getCheckPointParam());

      }

      JobID jobID = ExecuteSql.exeSql(sqlList, tEnv);

      System.out.println(SystemConstant.QUERY_JOBID_KEY_WORD + jobID);

      LOG.info(SystemConstant.QUERY_JOBID_KEY_WORD + "{}", jobID);

    } catch (Exception e) {
      System.err.println("任务执行失败:" + e.getMessage());
      LOG.error("任务执行失败：", e);
    }


  }


  private static JobRunParam buildParam(String[] args) throws Exception {
    ParameterTool parameterTool = ParameterTool.fromArgs(args);
    String sqlPath = parameterTool.get("sql");
    if (StringUtils.isEmpty(sqlPath)) {
      throw new NullPointerException("-sql参数 不能为空");
    }
    JobRunParam jobRunParam = new JobRunParam();
    jobRunParam.setSqlPath(sqlPath);
    jobRunParam.setCheckPointParam(CheckPointParams.buildCheckPointParam(parameterTool));
    String type = parameterTool.get("type");
    if (StringUtils.isNotEmpty(type)) {
      jobRunParam.setJobTypeEnum(JobTypeEnum.getJobTypeEnum(Integer.valueOf(type)));
    }
    return jobRunParam;
  }

}
