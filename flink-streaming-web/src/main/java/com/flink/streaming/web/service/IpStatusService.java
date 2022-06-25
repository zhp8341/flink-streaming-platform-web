package com.flink.streaming.web.service;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 02:26
 */
public interface IpStatusService {

  /**
   * 服务启动修改状态
   *
   * @author zhuhuipei
   * @date 2020-07-20
   * @time 02:27
   */
  void registerIp();


  /**
   * 服务关闭修改状态
   *
   * @author zhuhuipei
   * @date 2020-07-20
   * @time 02:27
   */
  void cancelIp();


  /**
   * 更新最后心跳时间
   *
   * @author zhuhuipei
   * @date 2020-09-22
   * @time 19:01
   */
  void updateHeartbeatBylocalIp();


  /**
   * 检查本机是不是leader
   *
   * @author zhuhuipei
   * @date 2020-09-22
   * @time 19:53
   */
  boolean isLeader();
}
