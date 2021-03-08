package com.flink.streaming.web.ao.impl;

import com.flink.streaming.web.ao.JobConfigAO;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.service.JobAlarmConfigService;
import com.flink.streaming.web.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/2/28
 * @time 11:25
 */
@Component
@Slf4j
public class JobConfigAOImpl implements JobConfigAO {

    @Autowired
    private JobConfigService jobConfigService;

    @Autowired
    private JobAlarmConfigService jobAlarmConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addJobConfig(JobConfigDTO jobConfigDTO) {
        Long jobConfigId = jobConfigService.addJobConfig(jobConfigDTO);
        jobAlarmConfigService.upSertBatchJobAlarmConfig(jobConfigDTO.getAlarmTypeEnumList(), jobConfigId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobConfigById(JobConfigDTO jobConfigDTO) {
        jobConfigService.updateJobConfigById(jobConfigDTO);
        jobAlarmConfigService.upSertBatchJobAlarmConfig(jobConfigDTO.getAlarmTypeEnumList(), jobConfigDTO.getId());
    }
}
