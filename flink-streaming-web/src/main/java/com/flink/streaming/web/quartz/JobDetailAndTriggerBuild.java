package com.flink.streaming.web.quartz;

import com.flink.streaming.web.common.SystemConstants;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/29
 */
public class JobDetailAndTriggerBuild {

  public static JobDetail buildJobDetail(Long id, String jobName) {
    JobDetail jobDetail = JobBuilder.newJob(JobExecute.class).withIdentity(
        SystemConstants.buildQuartzJobKeyName(id)).build();
    jobDetail.getJobDataMap().put("id", id);
    jobDetail.getJobDataMap().put("jobName", jobName);
    return jobDetail;
  }

  public static Trigger buildTrigger(String jobName, String cron) {
    Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
        .withIdentity(jobName)
        .build();
    return trigger;
  }

}
