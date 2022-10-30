package com.flink.streaming.web.quartz;

import com.flink.streaming.web.common.SystemConstants;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2022/10/29
 */
@Component
@Slf4j
@Data
public class SchedulerConfig {

  private Scheduler scheduler;

  @PostConstruct
  public void init() {
    this.createScheduler();
    this.startScheduler();
  }


  @SneakyThrows
  public Scheduler createScheduler() {
    log.info("####create scheduler....");
    if (scheduler == null) {
      scheduler = StdSchedulerFactory.getDefaultScheduler();
    }
    return scheduler;
  }

  @SneakyThrows
  public void startScheduler() {
    log.info("#####start scheduler....");
    if (scheduler != null && !scheduler.isStarted()) {
      scheduler.start();
    }
  }

  @SneakyThrows
  @PreDestroy
  public void stopScheduler() {
    if (scheduler != null && scheduler.isStarted()) {
      log.info("#####shutdown scheduler....");
      scheduler.shutdown(true);
    }
  }

  public void deleteJob(Long id) {
    try {
      JobKey jobKey = new JobKey(SystemConstants.buildQuartzJobKeyName(id));
      if (scheduler.checkExists(jobKey)) {
        log.info("取消调度任务id={}", id);
        scheduler.deleteJob(jobKey);
      } else {
        log.info("没有在调度器里面不用取消 id={}", id);
      }

    } catch (Exception e) {
      log.error("取消调度任务失败 id={}", id);
    }

  }

}
