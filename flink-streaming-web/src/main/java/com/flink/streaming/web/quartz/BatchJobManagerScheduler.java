package com.flink.streaming.web.quartz;

import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.model.entity.BatchJob;
import com.flink.streaming.web.service.JobConfigService;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 批任务调度管理 1、新增job 2、删除job 3、全量一次加载任务（启动的时候）
 *
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/29
 */
@Component
@Slf4j
public class BatchJobManagerScheduler implements ApplicationRunner {

  @Autowired
  private SchedulerConfig schedulerConfig;

  @Autowired
  private JobConfigService jobConfigService;

  /*
   *服务启动的时候注册批任务
   * @Param:[args]
   * @return: void
   * @Author: zhuhuipei
   * @date 2022/10/29
   */
  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("##########开始注册批任务调度##########");
    this.batchRegisterJob();
    log.info("##########结束注册批任务调度##########");
  }


  /*
   *注册单个任务到调度器
   * @Param:[batchJob]
   * @return: void
   * @Author: zhuhuipei
   * @date 2022/10/29
   */
  public void registerJob(BatchJob batchJob) {
    log.info("注册批任务到调度器 batchJob={}", batchJob);
    Scheduler scheduler = schedulerConfig.getScheduler();
    try {
      JobKey jobKey = new JobKey(SystemConstants.buildQuartzJobKeyName(batchJob.getId()));
      if (scheduler.checkExists(jobKey)) {
        log.info("任务已经存在不需要注册 batchJob={}", batchJob);
        return;
      }
      JobDetail jobDetail = JobDetailAndTriggerBuild
          .buildJobDetail(batchJob.getId(), batchJob.getJobName());
      Trigger trigger = JobDetailAndTriggerBuild
          .buildTrigger(batchJob.getJobName(), batchJob.getCron());
      scheduler.scheduleJob(jobDetail, trigger);
    } catch (Exception e) {
      log.error("registerJob is error batchJob={}", batchJob, e);
    }
    this.getJobKeysCount();


  }

  /*
   *删除一个job
   * @Param:[id]
   * @return: void
   * @Author: zhuhuipei
   * @date 2022/10/30
   */
  public void deleteJob(Long id) {
    schedulerConfig.deleteJob(id);
    this.getJobKeysCount();
  }

  /*
   *查询任务个数
   * @Param:[]
   * @return: java.lang.Integer
   * @Author: zhuhuipei
   * @date 2022/10/30
   */
  public Integer getJobKeysCount() {
    GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
    Set<JobKey> jobKeys = null;
    try {
      jobKeys = schedulerConfig.getScheduler().getJobKeys(matcher);
      log.info("当前调度器运行的任务有 {} 个", jobKeys.size());
      return jobKeys.size();
    } catch (Exception e) {
      log.error("getJobKeys is error");
    }
    return 0;
  }

  /*
   *批量注册
   * @Param:[]
   * @return: void
   * @Author: zhuhuipei
   * @date 2022/10/30
   */
  public void batchRegisterJob() {
    List<BatchJob> list = jobConfigService.getAllBatchJobs();
    if (CollectionUtils.isEmpty(list)) {
      log.info("没有找到批任务，不需要注册定时调度");
      return;
    }
    for (BatchJob batchJob : list) {
      if (!StringUtils.isEmpty(batchJob.getCron())) {
        this.registerJob(batchJob);
      }
    }
    log.info("本次批量注册完成后一共有 {} 个任务 ", this.getJobKeysCount());
  }

}
