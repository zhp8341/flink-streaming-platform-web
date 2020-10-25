package com.flink.streaming.web.ao.impl;

import com.flink.streaming.web.adapter.FlinkHttpRequestAdapter;
import com.flink.streaming.web.adapter.HttpRequestAdapter;
import com.flink.streaming.web.ao.AlarmServiceAO;
import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.ao.TaskServiceAO;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.YarnUtil;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.flink.JobInfo;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-22
 * @time 19:59
 */
@Component
@Slf4j
public class TaskServiceAOImpl implements TaskServiceAO {

    @Autowired
    private JobConfigService jobConfigService;

    @Autowired
    private FlinkHttpRequestAdapter flinkHttpRequestAdapter;

    @Autowired
    private HttpRequestAdapter httpRequestAdapter;

    @Autowired
    private AlarmServiceAO alarmServiceAO;

    @Autowired
    private JobServerAO jobServerAO;

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void checkJobStatusByYarn() {

        List<JobConfigDTO> jobConfigDTOList = jobConfigService.findJobConfigByStatus(JobConfigStatus.RUN.getCode());
        if (CollectionUtils.isEmpty(jobConfigDTOList)) {
            log.warn("当前配置中没有运行的任务");
            return;
        }

        for (JobConfigDTO jobConfigDTO : jobConfigDTOList) {
            //TODO 不同的deployMode 需要调用不同的接口查询 目前只有一种模式
            JobInfo jobInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
            if (jobInfo == null || !"RUNNING".equals(jobInfo.getStatus())) {
                log.error("发现本地任务状态和yarn上不一致,准备自动修复任务状态 jobInfo={}", jobInfo);
                JobConfigDTO jobConfig = new JobConfigDTO();
                jobConfig.setStauts(JobConfigStatus.STOP);
                jobConfig.setEditor("sys_auto");
                jobConfig.setId(jobConfigDTO.getId());
                jobConfig.setJobId("");
                jobConfigService.updateJobConfigById(jobConfig);

                alart(SystemConstants.buildDingdingMessage(" 检测到任务停止运行 任务名称：" + jobConfigDTO.getJobName()), jobConfigDTO.getId());
            }
            this.sleep();
        }

    }

    @Override
    public void checkYarnJobByStop() {
        List<JobConfigDTO> jobConfigDTOList = jobConfigService.findJobConfigByStatus(JobConfigStatus.STOP.getCode());
        if (CollectionUtils.isEmpty(jobConfigDTOList)) {
            return;
        }

        for (JobConfigDTO jobConfigDTO : jobConfigDTOList) {
            //TODO 不同的deployMode 需要调用不同的接口查询 目前只有一种模式
            String appId = null;
            try {
                String queueName = YarnUtil.getQueueName(jobConfigDTO.getFlinkRunConfig());
                if (StringUtils.isEmpty(queueName)) {
                    continue;
                }
                log.info("check job getJobName={} queueName={}",jobConfigDTO.getJobName(),queueName);
                appId = httpRequestAdapter.getAppIdByYarn(jobConfigDTO.getJobName(), queueName);
            } catch (BizException be) {
                if (SysErrorEnum.YARN_CODE.getCode().equals(be.getCode())) {
                    continue;
                }
                log.error("getAppIdByYarn is error ", be);
            } catch (Exception e) {
                log.error("getAppIdByYarn is error ", e);
                continue;
            }
            if (!StringUtils.isEmpty(appId)) {
                jobServerAO.stop(jobConfigDTO.getId(), "sys");
                alart(SystemConstants.buildDingdingMessage("kill掉yarn上任务保持数据一致性 任务名称：" + jobConfigDTO.getJobName()), jobConfigDTO.getId());
            }
        }


    }

    @Override
    public void autoSavePoint() {
        List<JobConfigDTO> jobConfigDTOList = jobConfigService.findJobConfigByStatus(JobConfigStatus.RUN.getCode());
        if (CollectionUtils.isEmpty(jobConfigDTOList)) {
            return;
        }
        for (JobConfigDTO jobConfigDTO : jobConfigDTOList) {
            //TODO 不同的deployMode 需要调用不同的接口查询 目前只有一种模式
            JobInfo jobInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
            if (jobInfo != null && "RUNNING".equals(jobInfo.getStatus())) {
                jobServerAO.savepoint(jobConfigDTO.getId());
            }
            this.sleep();
        }
    }


    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }


    private void alart(String content, Long jobConfigId) {
        try {
            String alartUrl = systemConfigService.getSystemConfigByKey(SysConfigEnum.DINGDING_ALARM_URL.getKey());
            if (StringUtils.isEmpty(alartUrl)) {
                log.warn("没有配置钉钉url地址 不发送告警");
                return;
            }
            alarmServiceAO.sendForDingding(alartUrl, content, jobConfigId);
        } catch (Exception e) {
            log.error("告警失败 is error");
        }
    }
}
