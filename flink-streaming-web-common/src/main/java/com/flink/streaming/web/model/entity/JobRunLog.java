package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhuhuipei
 * @date 2020-08-17
 * @time 00:14
 */
@Data
public class JobRunLog implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private Long jobConfigId;

  /**
   * 任务名称
   */
  private String jobName;

  /**
   * 任务描述
   */
  private String jobDesc;

  /**
   * 提交模式: standalone 、yarn 、yarn-session
   */
  private String deployMode;

  /**
   * 运行后的任务id
   */
  private String jobId;

  /**
   * 远程日志url的地址
   */
  private String remoteLogUrl;

  /**
   * 启动时间
   */
  private Date startTime;

  /**
   * 启动时间
   */
  private Date endTime;

  /**
   * 任务状态
   */
  private String jobStatus;

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
   * 启动时本地日志
   */
  private String localLog;

  private String runIp;


}
