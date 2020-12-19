package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author zhuhuipei
 * @date 2020-07-10
 * @time 01:46
 */
@Data
public class JobConfig{



    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * flink的本地目录
     */
    private String deployMode;

    /**
     * flink运行配置
     */
    private String flinkRunConfig;

    /**
     * flink运行配置
     */
    private String flinkCheckpointConfig;

    /**
     * flink运行配置
     */
    private String jobId;

    /**
     * 1:开启 0: 关闭
     */
    private Integer isOpen;

    /**
     * @see  com.flink.streaming.web.enums.JobConfigStatus
     * 1:运行中 0: 停止中 -1:运行失败
     */
    private Integer stauts;


    /**
     * 三方jar udf、 连接器 等jar如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String  extJarPath;

    /**
     * 最后一次启动时间
     */
    private Date lastStartTime;

    private Long lastRunLogId;

    /**
     * 更新版本号 用于乐观锁
     */
    private Integer version;

    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    private String creator;

    private String editor;

    /**
     * sql语句
     */
    private String flinkSql;



}
