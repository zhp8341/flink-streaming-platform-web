package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author
 */
@Data
public class AlartLog {

  private Long id;

  private Long jobConfigId;

  private String jobName;

  /**
   * 消息内容
   */
  private String message;

  /**
   * 1:钉钉
   */
  private Integer type;

  /**
   * 1:成功 0:失败
   */
  private Integer status;


  /**
   * 失败原因
   */
  private String failLog;

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

}
