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
import com.flink.streaming.web.model.flink.JobStandaloneInfo;
import com.flink.streaming.web.model.flink.JobYarnInfo;
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
    private JobServerAO jobYarnServerAO;

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
            switch (jobConfigDTO.getDeployModeEnum()) {
                case YARN_PER:
                    this.checkYarn(jobConfigDTO);
                    break;
                case LOCAL:
                    this.checkStandalone(jobConfigDTO);
                    break;
                case STANDALONE:
                    this.checkStandalone(jobConfigDTO);
                    break;
                default:
                    break;
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
            switch (jobConfigDTO.getDeployModeEnum()) {
                case YARN_PER:
                    String appId = null;
                    try {
                        String queueName = YarnUtil.getQueueName(jobConfigDTO.getFlinkRunConfig());
                        if (StringUtils.isEmpty(queueName)) {
                            continue;
                        }
                        log.info("check job getJobName={} queueName={}", jobConfigDTO.getJobName(), queueName);
                        appId = httpRequestAdapter.getAppIdByYarn(jobConfigDTO.getJobName(), queueName);
                    } catch (BizException be) {
                        if (SysErrorEnum.YARN_CODE.getCode().equals(be.getCode())) {
                            continue;
                        }
                        log.error("[BizException]getAppIdByYarn  is error ", be);
                    } catch (Exception e) {
                        log.error("[Exception]getAppIdByYarn is error ", e);
                        continue;
                    }
                    if (!StringUtils.isEmpty(appId)) {
                        JobYarnInfo jobYarnInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(appId);
                        if (jobYarnInfo != null && SystemConstants.STATUS_RUNNING.equals(jobYarnInfo.getStatus())) {
                            log.info("执行停止操作 jobYarnInfo={} id={}", jobYarnInfo, appId);
                            flinkHttpRequestAdapter.cancelJobForYarnByAppId(appId, jobYarnInfo.getId());
                        }
                        alart(SystemConstants.buildDingdingMessage("kill掉yarn上任务保持数据一致性 任务名称：" +
                                jobConfigDTO.getJobName()), jobConfigDTO.getId());
                    }
                    break;
                default:
                    break;
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
            switch (jobConfigDTO.getDeployModeEnum()) {
                case YARN_PER:
                    JobYarnInfo jobYarnInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
                    if (jobYarnInfo != null && SystemConstants.STATUS_RUNNING.equals(jobYarnInfo.getStatus())) {
                        jobYarnServerAO.savepoint(jobConfigDTO.getId());
                    }
                    this.sleep();
                    break;
                default:
                    break;
            }
        }
    }


    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }


    private void checkYarn(JobConfigDTO jobConfigDTO) {
        if (StringUtils.isEmpty(jobConfigDTO.getJobId())) {
            String message = SystemConstants.buildDingdingMessage(" 检测到任务jobId异常 任务名称："
                    + jobConfigDTO.getJobName());
            this.alart(message, jobConfigDTO.getId());
            log.error(message);
            return;
        }
        JobYarnInfo jobYarnInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
        if (jobYarnInfo == null || !SystemConstants.STATUS_RUNNING.equals(jobYarnInfo.getStatus())) {
            log.error("发现本地任务状态和yarn上不一致,准备自动修复任务状态 jobInfo={}", jobYarnInfo);
            JobConfigDTO jobConfig = new JobConfigDTO();
            jobConfig.setStauts(JobConfigStatus.STOP);
            jobConfig.setEditor("sys_auto");
            jobConfig.setId(jobConfigDTO.getId());
            jobConfig.setJobId("");
            jobConfigService.updateJobConfigById(jobConfig);
            this.alart(SystemConstants.buildDingdingMessage(" 检测到任务停止运行 任务名称：" +
                    jobConfigDTO.getJobName()), jobConfigDTO.getId());
        }
    }


    private void checkStandalone(JobConfigDTO jobConfigDTO) {
        if (StringUtils.isEmpty(jobConfigDTO.getJobId())) {
            String message = SystemConstants.buildDingdingMessage(" 检测到任务jobId异常 任务名称："
                    + jobConfigDTO.getJobName());
            this.alart(message, jobConfigDTO.getId());
            log.error(message);
            return;
        }
        JobStandaloneInfo jobStandaloneInfo = flinkHttpRequestAdapter.getJobInfoForStandaloneByAppId(jobConfigDTO.getJobId(), jobConfigDTO.getDeployModeEnum());
        if (jobStandaloneInfo == null || !SystemConstants.STATUS_RUNNING.equals(jobStandaloneInfo.getState())) {
            log.error("发现本地任务状态和yarn上不一致,准备自动修复任务状态 jobStandaloneInfo={}", jobStandaloneInfo);
            JobConfigDTO jobConfig = new JobConfigDTO();
            jobConfig.setStauts(JobConfigStatus.STOP);
            jobConfig.setEditor("sys_auto");
            jobConfig.setId(jobConfigDTO.getId());
            jobConfig.setJobId("");
            jobConfigService.updateJobConfigById(jobConfig);
            this.alart(SystemConstants.buildDingdingMessage(" 检测到任务停止运行 任务名称：" +
                    jobConfigDTO.getJobName()), jobConfigDTO.getId());
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
            log.error("告警失败 is error",e);
        }
    }
}
