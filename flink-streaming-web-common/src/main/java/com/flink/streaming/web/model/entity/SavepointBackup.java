package com.flink.streaming.web.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
@Data
public class SavepointBackup implements Serializable {

  private Long id;

  private Long jobConfigId;

  /**
   * 报错地址
   */
  private String savepointPath;

  private Integer isDeleted;

  private Date backupTime;

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

  private static final long serialVersionUID = 1L;

}
