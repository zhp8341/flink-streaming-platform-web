package com.flink.streaming.web.ao;

import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunParamDTO;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/3/28
 * @time 10:01
 */
public interface JobBaseServiceAO {

  /**
   * 提交任务前校验数据
   *
   * @author zhuhuipei
   * @date 2021/3/28
   * @time 10:12
   */
  void checkStart(JobConfigDTO jobConfigDTO);

  /**
   * Savepoint前校验数据
   *
   * @author zhuhuipei
   * @date 2021/3/31
   * @time 19:54
   */
  void checkSavepoint(JobConfigDTO jobConfigDTO);

  /**
   * 管配置检查
   *
   * @author zhuhuipei
   * @date 2021/3/31
   * @time 20:06
   */
  void checkClose(JobConfigDTO jobConfigDTO);

  /**
   * @author zhuhuipei
   * @date 2021/3/28
   * @time 10:12
   */
  Long insertJobRunLog(JobConfigDTO jobConfigDTO, String userName);


  /**
   * 将配置的sql 写入文件并且返回运行所需参数
   *
   * @author zhuhuipei
   * @date 2021/3/28
   * @time 10:37
   */
  JobRunParamDTO writeSqlToFile(JobConfigDTO jobConfigDTO);


  /**
   * 异步执行任务
   *
   * @author zhuhuipei
   * @date 2021/3/28
   * @time 10:55
   */
  void aSyncExecJob(final JobRunParamDTO jobRunParamDTO, final JobConfigDTO jobConfig,
      final Long jobRunLogId, final String savepointPath);


}
