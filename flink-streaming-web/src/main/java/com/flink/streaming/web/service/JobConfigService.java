package com.flink.streaming.web.service;

import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.entity.BatchJob;
import com.flink.streaming.web.model.param.JobConfigParam;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-14
 * @time 19:02
 */
public interface JobConfigService {

  /**
   * 新增配置 返回主键Id
   *
   * @author zhuhuipei
   * @date 2020-07-14
   * @time 19:26
   */
  Long addJobConfig(JobConfigDTO jobConfigDTO);


  /**
   * 修改配置
   *
   * @author zhuhuipei
   * @date 2020-07-14
   * @time 19:26
   */
  void updateJobConfigById(JobConfigDTO jobConfigDTO);

  /**
   * 修改配置(记录历史信息)
   *
   * @param jobConfigDTO
   * @author wxj
   * @date 2021年12月30日 下午2:33:31
   * @version V1.0
   */
  void updateJobConfigByIdWithWriteHistory(JobConfigDTO jobConfigDTO);


  /**
   * @author zhuhuipei
   * @date 2020-08-18
   * @time 19:01
   */
  void updateJobConfigStatusById(Long id, JobConfigStatus jobConfigStatus);


  /**
   * 启动状态更新 有乐观锁
   *
   * @author zhuhuipei
   * @date 2021/2/28
   * @time 17:57
   */
  void updateStatusByStart(Long id, String userName, Long jobRunLogId, Integer version);

  /**
   * 单个查询任务详情
   *
   * @author zhuhuipei
   * @date 2020-07-14
   * @time 23:05
   */
  JobConfigDTO getJobConfigById(Long id);

  /**
   * 单个查询任务详情(包括删除)
   *
   * @param id
   * @return
   * @author wxj
   * @date 2021年12月28日 下午1:48:07
   * @version V1.0
   */
  JobConfigDTO getJobConfigByIdContainDelete(Long id);

  /**
   * 开启或者配置
   *
   * @author zhuhuipei
   * @date 2020-07-14
   * @time 19:27
   */
  void openOrClose(Long id, YN yn, String userName);


  /**
   * 删除任务
   *
   * @author zhuhuipei
   * @date 2020-07-14
   * @time 23:03
   */
  void deleteJobConfigById(Long id, String userName);

  /**
   * 恢复删除任务
   *
   * @param id
   * @param userName
   * @author wxj
   * @date 2021年12月28日 下午2:07:31
   * @version V1.0
   */
  int recoveryDeleteJobConfigById(Long id, String userName);

  /**
   * 分页查询
   *
   * @author zhuhuipei
   * @date 2020-07-15
   * @time 02:04
   */
  PageModel<JobConfigDTO> queryJobConfig(JobConfigParam jobConfigParam);


  /**
   * 按状态获取任务
   *
   * @author zhuhuipei
   * @date 2020-09-22
   * @time 23:04
   */
  List<JobConfigDTO> findJobConfigByStatus(Integer... status);


  /*
   * 获取有效的批任务
   * @Param:[]
   * @return: java.util.List<com.flink.streaming.web.model.entity.BatchJob>
   * @Author: zhuhuipei
   * @date 2022/10/29
   */
  List<BatchJob> getAllBatchJobs();


}
