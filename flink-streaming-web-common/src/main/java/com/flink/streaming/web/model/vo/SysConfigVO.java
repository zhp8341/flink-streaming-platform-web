package com.flink.streaming.web.model.vo;

import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-23
 * @time 00:07
 */
@Data
public class SysConfigVO {

  private String key;

  private String desc;

  public SysConfigVO(String key, String desc) {
    this.key = key;
    this.desc = desc;
  }
}
