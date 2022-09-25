package com.flink.streaming.web.model.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class UploadFile implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long id;
  /**
   * 文件名字
   */
  private String fileName;
  /**
   * 文件路径
   */
  private String filePath;
  /**
   * 1:jar
   */
  private Integer type;
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