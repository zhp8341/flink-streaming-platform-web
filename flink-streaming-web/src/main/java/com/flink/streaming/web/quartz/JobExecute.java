package com.flink.streaming.web.quartz;

import com.flink.streaming.common.enums.JobTypeEnum;
import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.factory.ApplicationContextProvider;
import com.flink.streaming.web.factory.JobServerAOFactory;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 批任务调用入口
 *
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/29
 */
@Slf4j
@Component
@Lazy
public class JobExecute implements Job {

  private static final String NAME = "quartz-job";

  private JobConfigService jobConfigService = ApplicationContextProvider
      .getBean(JobConfigService.class);

  @Override
  public void execute(JobExecutionContext context) {
    log.info("开始执行定时任务");
    Long id = null;
    try {
      id = (Long) context.getJobDetail().getJobDataMap().get("id");
      String jobName = (String) context.getJobDetail().getJobDataMap().get("jobName");
      JobServerAO jobServerAO = this.getJobServerAO(id);
      jobServerAO.start(id, null, NAME);
    } catch (Exception e) {
      log.error("JobExecute-execute is error id={}", id, e);
    }
  }

  private JobServerAO getJobServerAO(Long id) {
    JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
    if (jobConfigDTO == null || jobConfigDTO.getJobTypeEnum() != JobTypeEnum.SQL_BATCH) {
      log.error("不是批任务或者任务不存在 不能执行定时调度 id={} jobConfigDTO={}", id, jobConfigDTO);
      throw new NullPointerException("getJobServerAO is null");
    }
    if (jobConfigDTO.getStatus() == JobConfigStatus.RUN
        || jobConfigDTO.getStatus() == JobConfigStatus.STARTING) {
      throw new RuntimeException("bath_sql is run");
    }

    return JobServerAOFactory.getJobServerAO(jobConfigDTO.getDeployModeEnum());


  }
}
