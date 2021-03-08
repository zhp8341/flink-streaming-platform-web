package com.flink.streaming.web.ao.impl;

import com.flink.streaming.web.adapter.FlinkHttpRequestAdapter;
import com.flink.streaming.web.adapter.HttpRequestAdapter;
import com.flink.streaming.web.ao.AlarmServiceAO;
import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.ao.TaskServiceAO;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.YarnUtil;
import com.flink.streaming.web.config.AlarmPoolConfig;
import com.flink.streaming.web.enums.*;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.flink.JobStandaloneInfo;
import com.flink.streaming.web.model.flink.JobYarnInfo;
import com.flink.streaming.web.model.vo.CallbackDTO;
import com.flink.streaming.web.service.JobAlarmConfigService;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.SystemConfigService;
import com.flink.streaming.web.thread.AlarmDingdingThread;
import com.flink.streaming.web.thread.AlarmHttpThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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
    private JobServerAO jobStandaloneServerAO;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private JobAlarmConfigService jobAlarmConfigService;


    private ThreadPoolExecutor threadPoolExecutor = AlarmPoolConfig.getInstance().getThreadPoolExecutor();

    @Override
    public void checkJobStatusByYarn() {
        List<JobConfigDTO> jobConfigDTOList = jobConfigService.findJobConfigByStatus(JobConfigStatus.RUN.getCode());
        if (CollectionUtils.isEmpty(jobConfigDTOList)) {
            log.warn("当前配置中没有运行的任务");
            return;
        }
        for (JobConfigDTO jobConfigDTO : jobConfigDTOList) {
            List<AlarmTypeEnum> alarmTypeEnumList = jobAlarmConfigService.findByJobId(jobConfigDTO.getId());
            switch (jobConfigDTO.getDeployModeEnum()) {
                case YARN_PER:
                    this.checkYarn(jobConfigDTO, alarmTypeEnumList);
                    break;
                case LOCAL:
                    this.checkStandalone(jobConfigDTO, alarmTypeEnumList);
                    break;
                case STANDALONE:
                    this.checkStandalone(jobConfigDTO, alarmTypeEnumList);
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
                            log.warn("执行停止操作 jobYarnInfo={} id={}", jobYarnInfo, appId);
                            flinkHttpRequestAdapter.cancelJobForYarnByAppId(appId, jobYarnInfo.getId());
                        }
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
            log.error("autoSavePoint is error  没有找到运行中的任务 ");
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


    private void checkYarn(JobConfigDTO jobConfigDTO, List<AlarmTypeEnum> alarmTypeEnumList) {
        if (StringUtils.isEmpty(jobConfigDTO.getJobId())) {
            log.error("任务配置不存在");
            return;
        }
        //查询任务状态
        JobYarnInfo jobYarnInfo = flinkHttpRequestAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());

        if (jobYarnInfo != null && SystemConstants.STATUS_RUNNING.equals(jobYarnInfo.getStatus())) {
            return;
        }

        //变更任务状态
        log.error("发现本地任务状态和yarn上不一致,准备自动修复本地web任务状态 jobInfo={}", jobYarnInfo);
        JobConfigDTO jobConfig = JobConfigDTO.bulidStop(jobConfigDTO.getId());
        jobConfigService.updateJobConfigById(jobConfig);

        //发送告警并且自动拉起任务
        this.alermAndAutoJob(alarmTypeEnumList,
                SystemConstants.buildDingdingMessage(" 检测到任务停止运行 任务名称：" +
                        jobConfigDTO.getJobName()), jobConfigDTO, DeployModeEnum.YARN_PER);


    }


    private void checkStandalone(JobConfigDTO jobConfigDTO, List<AlarmTypeEnum> alarmTypeEnumList) {
        if (StringUtils.isEmpty(jobConfigDTO.getJobId())) {
            String message = SystemConstants.buildDingdingMessage(" 检测到任务jobId异常 任务名称："
                    + jobConfigDTO.getJobName());
            log.error(message);
            return;
        }
        //查询任务状态
        JobStandaloneInfo jobStandaloneInfo = flinkHttpRequestAdapter.getJobInfoForStandaloneByAppId(jobConfigDTO.getJobId(),
                jobConfigDTO.getDeployModeEnum());

        if (jobStandaloneInfo != null && SystemConstants.STATUS_RUNNING.equals(jobStandaloneInfo.getState())) {
            return;
        }

        //变更任务状态
        log.error("发现本地任务状态和yarn上不一致,准备自动修复任务状态 jobStandaloneInfo={}", jobStandaloneInfo);
        JobConfigDTO jobConfig = JobConfigDTO.bulidStop(jobConfigDTO.getId());
        jobConfigService.updateJobConfigById(jobConfig);

        //发送告警并且自动拉起任务
        this.alermAndAutoJob(alarmTypeEnumList,
                SystemConstants.buildDingdingMessage(" 检测到任务停止运行 任务名称：" +
                        jobConfigDTO.getJobName()), jobConfigDTO, DeployModeEnum.STANDALONE);
    }


    /**
     * 告警并且拉起任务，
     * //TODO 如果拉起失败下次将不能拉起
     *
     * @author zhuhuipei
     * @date 2021/2/28
     * @time 19:50
     */
    private void alermAndAutoJob(List<AlarmTypeEnum> alarmTypeEnumList, String cusContent,
                                 JobConfigDTO jobConfigDTO, DeployModeEnum deployModeEnum) {


        if (CollectionUtils.isEmpty(alarmTypeEnumList)) {
            log.warn("没有配置告警，无法进行告警！！！");
            return;
        }

        CallbackDTO callbackDTO = CallbackDTO.to(jobConfigDTO);
        if (CollectionUtils.isEmpty(alarmTypeEnumList)) {
            return;
        }
        //告警
        for (AlarmTypeEnum alarmTypeEnum : alarmTypeEnumList) {
            switch (alarmTypeEnum) {
                case DINGDING:
                    this.dingdingAlarm(cusContent, callbackDTO.getJobConfigId());
                    break;
                case CALLBACK_URL:
                    this.httpAlarm(callbackDTO);
                    break;
            }
        }
        //自动拉起
        if (alarmTypeEnumList.contains(AlarmTypeEnum.AUTO_START_JOB)) {
            log.info("校验任务不存在,开始自动拉起 JobConfigId={}", callbackDTO.getJobConfigId());
            try {
                switch (deployModeEnum) {
                    case YARN_PER:
                        jobYarnServerAO.start(callbackDTO.getJobConfigId(), null, SystemConstants.USER_NAME_TASK_AUTO);
                        break;
                    case STANDALONE:
                        jobStandaloneServerAO.start(callbackDTO.getJobConfigId(), null, SystemConstants.USER_NAME_TASK_AUTO);
                        break;
                }

            } catch (Exception e) {
                log.error("自动重启任务失败 JobConfigId={}", callbackDTO.getJobConfigId(), e);
            }

        }

    }


    /**
     * 钉钉告警
     *
     * @author zhuhuipei
     * @date 2021/2/28
     * @time 19:56
     */
    private void dingdingAlarm(String content, Long jobConfigId) {
        String alartUrl = systemConfigService.getSystemConfigByKey(SysConfigEnum.DINGDING_ALARM_URL.getKey());
        if (StringUtils.isEmpty(alartUrl)) {
            log.warn("#####钉钉告警url没有设置，无法告警#####");
            return;
        }
        threadPoolExecutor.execute(new AlarmDingdingThread(alarmServiceAO, content, jobConfigId, alartUrl));
    }

    /**
     * 回调函数自定义告警
     *
     * @author zhuhuipei
     * @date 2021/2/28
     * @time 19:56
     */
    private void httpAlarm(CallbackDTO callbackDTO) {
        String alartUrl = systemConfigService.getSystemConfigByKey(SysConfigEnum.CALLBACK_ALARM_URL.getKey());
        if (StringUtils.isEmpty(alartUrl)) {
            log.warn("#####回调告警url没有设置，无法告警#####");
            return;
        }
        threadPoolExecutor.execute(new AlarmHttpThread(alarmServiceAO, callbackDTO, alartUrl));
    }

}
