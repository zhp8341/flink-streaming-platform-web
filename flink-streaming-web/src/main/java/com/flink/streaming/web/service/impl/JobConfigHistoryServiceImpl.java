package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.mapper.JobConfigHistoryMapper;
import com.flink.streaming.web.model.dto.JobConfigHistoryDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.entity.JobConfigHistory;
import com.flink.streaming.web.model.param.JobConfigHisotryParam;
import com.flink.streaming.web.service.JobConfigHistoryService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/5/5
 * @time 20:11
 */
@Service
@Log
public class JobConfigHistoryServiceImpl implements JobConfigHistoryService {

  @Autowired
  private JobConfigHistoryMapper jobConfigHistoryMapper;

  @Override
  public void insertJobConfigHistory(JobConfigHistoryDTO jobConfigHistoryDTO) {
    jobConfigHistoryMapper.insert(JobConfigHistoryDTO.toEntity(jobConfigHistoryDTO));
  }

  @Override
  public List<JobConfigHistoryDTO> getJobConfigHistoryByJobConfigId(Long jobConfigId) {
    return JobConfigHistoryDTO.toListDTO(jobConfigHistoryMapper.selectByJobConfigId(jobConfigId));
  }

  @Override
  public JobConfigHistoryDTO getJobConfigHistoryById(Long id) {
    return JobConfigHistoryDTO.toDTO(jobConfigHistoryMapper.selectById(id));
  }

  @Override
  public PageModel<JobConfigHistoryDTO> queryJobConfigHistory(
      JobConfigHisotryParam jobConfigParam) {
    if (jobConfigParam == null) {
      jobConfigParam = new JobConfigHisotryParam();
    }
    PageHelper.startPage(jobConfigParam.getPageNum(), jobConfigParam.getPageSize(), YN.Y.getCode());
    Page<JobConfigHistory> page = jobConfigHistoryMapper.findJobConfigHistory(jobConfigParam);
    if (page == null) {
      return null;
    }
    PageModel<JobConfigHistoryDTO> pageModel = new PageModel<JobConfigHistoryDTO>();
    pageModel.setPageNum(page.getPageNum());
    pageModel.setPages(page.getPages());
    pageModel.setPageSize(page.getPageSize());
    pageModel.setTotal(page.getTotal());
    pageModel.addAll(JobConfigHistoryDTO.toListDTO(page.getResult()));
    return pageModel;
  }
}
