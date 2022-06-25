package com.flink.streaming.web.enums;

import lombok.Getter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-18
 * @time 19:13
 */
@Getter
public enum JobStatusEnum {

  SUCCESS("成功"), FAIL("失败"), STARTING("启动中"), ERROR("未知异常");

  private String desc;

  JobStatusEnum(String desc) {
    this.desc = desc;
  }

  public static JobStatusEnum getJobStatusEnum(String name) {
    for (JobStatusEnum jobStatusEnum : JobStatusEnum.values()) {
      if (jobStatusEnum.name().equals(name)) {
        return jobStatusEnum;
      }
    }
    return JobStatusEnum.ERROR;
  }
}
