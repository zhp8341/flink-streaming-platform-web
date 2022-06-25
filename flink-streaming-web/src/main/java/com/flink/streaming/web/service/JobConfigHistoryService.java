package com.flink.streaming.web.service;

import com.flink.streaming.web.model.dto.JobConfigHistoryDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.JobConfigHisotryParam;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/5/5
 * @time 20:11
 */
public interface JobConfigHistoryService {

  /**
   * 新增记录
   *
   * @author zhuhuipei
   * @date 2021/5/5
   * @time 20:13
   */
  void insertJobConfigHistory(JobConfigHistoryDTO jobConfigHistoryDTO);


  /**
   * 查询历史记录
   *
   * @author zhuhuipei
   * @date 2021/5/5
   * @time 20:13
   */
  List<JobConfigHistoryDTO> getJobConfigHistoryByJobConfigId(Long jobConfigId);


  /**
   * 详情
   *
   * @author zhuhuipei
   * @date 2021/5/5
   * @time 20:14
   */
  JobConfigHistoryDTO getJobConfigHistoryById(Long id);

  /**
   * 分页查询
   *
   * @param jobConfigParam
   * @return
   * @author wxj
   * @date 2021年12月20日 上午11:11:56
   * @version V1.0
   */
  PageModel<JobConfigHistoryDTO> queryJobConfigHistory(JobConfigHisotryParam jobConfigParam);

}
