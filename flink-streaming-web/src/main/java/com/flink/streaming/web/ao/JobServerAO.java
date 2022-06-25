package com.flink.streaming.web.ao;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 23:11
 */
public interface JobServerAO {

  /**
   * 启动任务
   *
   * @author zhuhuipei
   * @date 2020-07-20
   * @time 23:12
   */
  void start(Long id, Long savepointId, String userName);


  /**
   * 关闭任务
   *
   * @author zhuhuipei
   * @date 2020-07-20
   * @time 23:12
   */
  void stop(Long id, String userName);


  /**
   * 执行savepoint
   *
   * @author zhuhuipei
   * @date 2020-09-21
   * @time 00:41
   */
  void savepoint(Long id);


  /**
   * 开启配置
   *
   * @author zhuhuipei
   * @date 2020-08-12
   * @time 21:14
   */
  void open(Long id, String userName);

  /**
   * 关闭配置
   *
   * @author zhuhuipei
   * @date 2020-08-12
   * @time 21:14
   */
  void close(Long id, String userName);
}
