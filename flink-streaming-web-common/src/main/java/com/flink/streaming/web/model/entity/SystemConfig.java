package com.flink.streaming.web.model.entity;

import com.flink.streaming.web.enums.SysConfigEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhuhuipei
 * @date 2020-07-20
 * @time 23:37
 */
@Data
public class SystemConfig implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long id;

  private String key;

  private String val;

  private String type;

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

  public SystemConfig() {

  }

  public SystemConfig(String key, String val) {
    this.key = key;
    this.val = val;
    this.type = SysConfigEnum.getType(key);
  }


}
