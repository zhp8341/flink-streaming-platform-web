package com.flink.streaming.web.ao.impl;

import com.alibaba.fastjson.JSON;
import com.flink.streaming.web.alart.DingDingAlarm;
import com.flink.streaming.web.alart.HttpAlarm;
import com.flink.streaming.web.ao.AlarmServiceAO;
import com.flink.streaming.web.enums.AlarmLogStatusEnum;
import com.flink.streaming.web.enums.AlarmLogTypeEnum;
import com.flink.streaming.web.model.dto.AlartLogDTO;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import com.flink.streaming.web.model.vo.CallbackDTO;
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


    @Autowired
    private HttpAlarm httpAlarm;

    @Override
    public boolean sendForDingding(String url, String content, Long jobConfigId) {

        boolean isSuccess = false;
        String failLog = "";
        try {
            isSuccess = dingDingAlarm.send(url, content);
        } catch (Exception e) {
            log.error("dingDingAlarm.send is error", e);
            failLog = e.getMessage();
        }
        this.insertLog(isSuccess, jobConfigId, failLog, content, AlarmLogTypeEnum.DINGDING);

        return isSuccess;
    }

    @Override
    public boolean sendForHttp(String url, CallbackDTO callbackDTO) {

        boolean isSuccess = false;
        String failLog = "";
        try {
            isSuccess = httpAlarm.send(url, callbackDTO);
        } catch (Exception e) {
            log.error("dingDingAlarm.send is error", e);
            failLog = e.getMessage();
        }
        this.insertLog(isSuccess, callbackDTO.getJobConfigId(), failLog, JSON.toJSONString(callbackDTO),
                AlarmLogTypeEnum.CALLBACK_URL);

        return isSuccess;
    }


    private void insertLog(boolean isSuccess, Long jobConfigId, String failLog, String content,
                           AlarmLogTypeEnum alarMLogTypeEnum) {
        JobRunLogDTO jobRunLogDTO = jobRunLogService.getDetailLogById(jobConfigId);
        AlartLogDTO alartLogDTO = new AlartLogDTO();
        alartLogDTO.setJobConfigId(jobConfigId);
        alartLogDTO.setAlarMLogTypeEnum(alarMLogTypeEnum);
        alartLogDTO.setMessage(content);
        if (jobRunLogDTO != null) {
            alartLogDTO.setJobName(jobRunLogDTO.getJobName());
        } else {
            alartLogDTO.setJobName("测试");
        }
        if (isSuccess) {
            alartLogDTO.setAlarmLogStatusEnum(AlarmLogStatusEnum.SUCCESS);
        } else {
            alartLogDTO.setAlarmLogStatusEnum(AlarmLogStatusEnum.FAIL);
            alartLogDTO.setFailLog(failLog);
        }
        alartLogService.addAlartLog(alartLogDTO);

    }
}
