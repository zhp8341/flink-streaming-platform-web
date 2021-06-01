package com.flink.streaming.web.service;

import com.flink.streaming.web.model.dto.JobConfigHistoryDTO;

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

}
