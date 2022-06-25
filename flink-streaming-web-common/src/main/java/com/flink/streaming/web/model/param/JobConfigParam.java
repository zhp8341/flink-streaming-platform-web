package com.flink.streaming.web.model.param;

import com.flink.streaming.web.model.page.PageParam;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-15
 * @time 02:04
 */
@Data
public class JobConfigParam extends PageParam {

  /**
   * @author zhuhuipei
   * @date 2020-07-15
   * @time 02:07
   * @see com.flink.streaming.web.enums.JobConfigStatus
   */
  private Integer status;


  /**
   * 任务名称
   */
  private String jobName;


  /**
   * 任务类型 0:sql 1:自定义jar'
   */
  private Integer jobType;


  private Integer open;

  private String jobId;

  private String deployMode;
}
