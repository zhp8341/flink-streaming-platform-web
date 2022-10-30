package com.flink.streaming.web.model.entity;

import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/29
 */
@Data
public class BatchJob {

  public BatchJob() {
  }

  public BatchJob(Long id, String jobName, String cron) {
    this.id = id;
    this.jobName = jobName;
    this.cron = cron;
  }

  private Long id;

  /**
   * 任务名称
   */
  private String jobName;

  /**
   * cron表达式
   */
  private String cron;

}
