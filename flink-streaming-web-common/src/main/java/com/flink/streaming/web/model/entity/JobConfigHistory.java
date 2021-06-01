package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author zhuhuipei
 * @date 2021/5/5
 * @time 19:49
 */
@Data
public class JobConfigHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * job_config主表Id
     */
    private Long jobConfigId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 提交模式: standalone 、yarn 、yarn-session 
     */
    private String deployMode;

    /**
     * flink运行配置
     */
    private String flinkRunConfig;

    /**
     * checkPoint配置
     */
    private String flinkCheckpointConfig;

    /**
     * udf地址及连接器jar 如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String extJarPath;

    /**
     * 更新版本号
     */
    private Integer version;

    private Boolean isDeleted;

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
