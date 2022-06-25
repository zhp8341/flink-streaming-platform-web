package com.flink.streaming.web.service;

import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.JobRunLogParam;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-17
 * @time 00:29
 */
public interface JobRunLogService {

  /**
   * 新增任务 返回主键
   *
   * @author zhuhuipei
   * @date 2020-08-17
   * @time 00:33
   */
  Long insertJobRunLog(JobRunLogDTO jobRunLogDTO);


  /**
   * 日志更新
   *
   * @author zhuhuipei
   * @date 2020-08-24
   * @time 21:08
   */
  void updateLogById(String log, Long id);

  /**
   * 更新
   *
   * @author zhuhuipei
   * @date 2020-08-18
   * @time 19:17
   */
  void updateJobRunLogById(JobRunLogDTO jobRunLogDTO);

  /**
   * 日志功能查询
   *
   * @author zhuhuipei
   * @date 2020-08-17
   * @time 20:45
   */
  PageModel<JobRunLogDTO> queryJobRunLog(JobRunLogParam jobRunLogParam);


  /**
   * 单个日志详情
   *
   * @author zhuhuipei
   * @date 2020-08-17
   * @time 19:49
   */
  JobRunLogDTO getDetailLogById(Long id);


  /**
   * 删除日志
   *
   * @author zhuhuipei
   * @date 2020-08-30
   * @time 23:44
   */
  void deleteLogByJobConfigId(Long jobConfigId);


}
