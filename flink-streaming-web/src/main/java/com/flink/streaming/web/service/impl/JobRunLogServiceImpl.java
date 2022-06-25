package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.mapper.JobRunLogMapper;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.entity.JobRunLog;
import com.flink.streaming.web.model.param.JobRunLogParam;
import com.flink.streaming.web.service.JobRunLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-17
 * @time 00:29
 */
@Slf4j
@Service
public class JobRunLogServiceImpl implements JobRunLogService {

  @Autowired
  private JobRunLogMapper jobRunLogMapper;

  @Override
  public Long insertJobRunLog(JobRunLogDTO jobRunLogDTO) {
    JobRunLog jobRunLog = JobRunLogDTO.toEntity(jobRunLogDTO);
    jobRunLogMapper.insert(jobRunLog);
    return jobRunLog.getId();
  }

  @Override
  public void updateLogById(String localLog, Long id) {
    try {
      JobRunLog jobRunLog = new JobRunLog();
      jobRunLog.setId(id);
      jobRunLog.setLocalLog(localLog);
      jobRunLogMapper.update(jobRunLog);
    } catch (Exception e) {
      log.error("更新日志 失败 id={} ,localLog={}", id, localLog, e);
    }

  }

  @Override
  public void updateJobRunLogById(JobRunLogDTO jobRunLogDTO) {
    jobRunLogMapper.update(JobRunLogDTO.toEntity(jobRunLogDTO));
  }

  @Override
  public PageModel<JobRunLogDTO> queryJobRunLog(JobRunLogParam jobRunLogParam) {
    if (jobRunLogParam == null) {
      jobRunLogParam = new JobRunLogParam();
    }
    PageHelper.startPage(jobRunLogParam.getPageNum(), jobRunLogParam.getPageSize(), YN.Y.getCode());

    //只能查最近30天的
    Page<JobRunLog> page = jobRunLogMapper.selectByParam(jobRunLogParam);
    if (page == null) {
      return null;
    }
    PageModel<JobRunLogDTO> pageModel = new PageModel<>();
    pageModel.setPages(page.getPages());
    pageModel.addAll(JobRunLogDTO.toListDTO(page.getResult()));
    pageModel.setPageSize(page.getPageSize());
    pageModel.setTotal(page.getTotal());
    pageModel.setPageNum(page.getPageNum());
    return pageModel;
  }

  @Override
  public JobRunLogDTO getDetailLogById(Long id) {
    return JobRunLogDTO.toDTO(jobRunLogMapper.selectById(id));
  }

  @Override
  public void deleteLogByJobConfigId(Long jobConfigId) {
    jobRunLogMapper.deleteByJobConfigId(jobConfigId);
  }


}
