package com.flink.streaming.web.model.param;

import com.flink.streaming.web.model.page.PageParam;
import lombok.Data;

/**
 * @author wxj
 * @version V1.0
 * @date 2021年12月20日 上午11:23:11
 */
@Data
public class JobConfigHisotryParam extends PageParam {

  /**
   * 任务编号
   */
  private Integer jobConfigId;


  /**
   * 任务名称
   */
  private String jobName;
}
