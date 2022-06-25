package com.flink.streaming.web.model.param;

import com.flink.streaming.web.model.page.PageParam;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-17
 * @time 00:18
 */
@Data
public class JobRunLogParam extends PageParam {

  private Long jobConfigId;

  /**
   * 运行后的任务id
   */
  private String jobId;

  private String jobName;
}
