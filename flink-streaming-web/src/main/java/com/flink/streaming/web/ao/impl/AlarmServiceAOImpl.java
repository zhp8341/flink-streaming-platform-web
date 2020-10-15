package com.flink.streaming.web.ao.impl;

import com.flink.streaming.web.alart.DingDingAlarm;
import com.flink.streaming.web.ao.AlarmServiceAO;
import com.flink.streaming.web.enums.AlartLogStatusEnum;
import com.flink.streaming.web.enums.AlartLogTypeEnum;
import com.flink.streaming.web.model.dto.AlartLogDTO;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.service.AlartLogService;
import com.flink.streaming.web.service.JobRunLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 19:50
 */
@Component
@Slf4j
public class AlarmServiceAOImpl implements AlarmServiceAO {


    @Autowired
    private AlartLogService alartLogService;

    @Autowired
    private JobRunLogService jobRunLogService;


    @Autowired
    private DingDingAlarm dingDingAlarm;

    @Override
    public boolean sendForDingding(String url, String content, Long jobConfigId) {

        JobRunLogDTO jobRunLogDTO = jobRunLogService.getDetailLogById(jobConfigId);

        boolean isSuccess = false;

        String failLog = "";

        try {
            isSuccess = dingDingAlarm.send(url, content);
        } catch (Exception e) {
            log.error("dingDingAlarm.send is error", e);
            failLog = e.getMessage();
        }
        AlartLogDTO alartLogDTO = new AlartLogDTO();
        alartLogDTO.setJobConfigId(jobConfigId);
        alartLogDTO.setAlartLogTypeEnum(AlartLogTypeEnum.DINGDING);
        alartLogDTO.setMessage(content);
        if (jobRunLogDTO != null) {
            alartLogDTO.setJobName(jobRunLogDTO.getJobName());
        } else {
            alartLogDTO.setJobName("测试");
        }
        if (isSuccess) {
            alartLogDTO.setAlartLogStatusEnum(AlartLogStatusEnum.SUCCESS);
        } else {
            alartLogDTO.setAlartLogStatusEnum(AlartLogStatusEnum.FAIL);
            alartLogDTO.setFailLog(failLog);
        }
        alartLogService.addAlartLog(alartLogDTO);

        return isSuccess;
    }
}
