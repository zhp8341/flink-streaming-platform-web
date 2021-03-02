package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.enums.AlarmTypeEnum;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.mapper.JobAlarmConfigMapper;
import com.flink.streaming.web.model.entity.JobAlarmConfig;
import com.flink.streaming.web.service.JobAlarmConfigService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/2/27
 * @time 17:47
 */
@Slf4j
@Service
public class JobAlarmConfigServiceImpl implements JobAlarmConfigService {

    @Autowired
    private JobAlarmConfigMapper jobAlarmConfigMapper;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upSertBatchJobAlarmConfig(List<AlarmTypeEnum> alarmTypeEnumList, Long jobId) {
        if (jobId == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_PARAM_IS_NULL);
        }
        this.checkSysConfig(alarmTypeEnumList);
        jobAlarmConfigMapper.deleteByJobId(jobId);
        if (CollectionUtils.isNotEmpty(alarmTypeEnumList)) {
            List<JobAlarmConfig> list = new ArrayList<>();
            for (AlarmTypeEnum alarmTypeEnum : alarmTypeEnumList) {
                JobAlarmConfig jobAlarmConfig = new JobAlarmConfig();
                jobAlarmConfig.setJobId(jobId);
                jobAlarmConfig.setType(alarmTypeEnum.getCode());
                list.add(jobAlarmConfig);
            }
            jobAlarmConfigMapper.insertBatch(list);
        }


    }

    @Override
    public List<AlarmTypeEnum> findByJobId(Long jobId) {

        List<JobAlarmConfig> list = jobAlarmConfigMapper.selectByJobId(jobId);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<AlarmTypeEnum> alarmTypeEnumList = new ArrayList<>();

        for (JobAlarmConfig jobAlarmConfig : list) {
            alarmTypeEnumList.add(AlarmTypeEnum.getAlarmTypeEnum(jobAlarmConfig.getType()));
        }

        return alarmTypeEnumList;
    }

    private void checkSysConfig(List<AlarmTypeEnum> alarmTypeEnumList) {
        if (CollectionUtils.isEmpty(alarmTypeEnumList)) {
            return;
        }
        for (AlarmTypeEnum alarmTypeEnum : alarmTypeEnumList) {
            switch (alarmTypeEnum) {
                case DINGDING:
                    if (!systemConfigService.isExist(SysConfigEnum.DINGDING_ALARM_URL.getKey())) {
                        throw new BizException(SysErrorEnum.ALARM_DINGDING_NULL);
                    }
                    break;
                case CALLBACK_URL:
                    if (!systemConfigService.isExist(SysConfigEnum.CALLBACK_ALARM_URL.getKey())) {
                        throw new BizException(SysErrorEnum.ALARM_HTTP_NULL);
                    }
                    break;
            }
        }
    }

}
