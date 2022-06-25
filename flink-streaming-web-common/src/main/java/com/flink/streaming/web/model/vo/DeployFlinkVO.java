package com.flink.streaming.web.model.vo;

import java.util.List;

import com.flink.streaming.common.enums.JobTypeEnum;
import com.flink.streaming.web.enums.AlarmTypeEnum;
import com.flink.streaming.web.enums.DeployModeEnum;

import lombok.Data;

@Data
public class DeployFlinkVO {

  @Data
  public static class FlinkTask {

    /**
     * 任务编号
     */
    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务描述
     */
    private String jobDesc;

    /**
     * sql语句
     */
    private String sqlFile;

    /**
     * 任务类型：SQL_STREAMING, JAR, SQL_BATCH; 为空默认为：SQL_STREAMING
     */
    private JobTypeEnum jobType;

    /**
     * 运行模式：YARN_PER, STANDALONE, LOCAL; 为空默认为：STANDALONE
     */
    private DeployModeEnum deployMode;

    private String flinkRunConfig;

    private String flinkCheckpointConfig;

    private String extJarPath;

    /**
     * 启动jar可能需要使用的自定义参数
     */
    private String customArgs;

    /**
     * 程序入口类
     */
    private String customMainClass;

    /**
     * 自定义jar的http地址 如:http://ccblog.cn/xx.jar
     */
    private String customJarUrl;

    /**
     * 钉钉告警、回调、自动重启（DINGDING、CALLBACK_URL、AUTO_START_JOB）
     */
    private List<AlarmTypeEnum> alarmTypes;

    /**
     * 发布启动标记：默认为true，表示发布时自动启动
     */
    private Boolean deployStartFlag;

  }

  private List<FlinkTask> taskList;

}
