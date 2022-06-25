package com.flink.streaming.web.rpc;

import com.flink.streaming.web.enums.DeployModeEnum;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 20:09
 */
public interface CommandRpcClinetAdapter {


  /**
   * 提交服务
   *
   * @author zhuhuipei
   * @date 2021/3/26
   * @time 17:31
   */
  String submitJob(String command, StringBuilder localLog, Long jobRunLogId,
      DeployModeEnum deployModeEnum)
      throws Exception;


  /**
   * yarn per模式执行savepoint
   * <p>
   * 默认savepoint保存的地址是：hdfs:///flink/savepoint/flink-streaming-platform-web/
   *
   * @author zhuhuipei
   * @date 2020-09-21
   * @time 23:14
   */
  void savepointForPerYarn(String jobId, String targetDirectory, String yarnAppId) throws Exception;

  /**
   * 集群模式下执行savepoint
   *
   * @author zhuhuipei
   * @date 2021/3/31
   * @time 19:39
   */
  void savepointForPerCluster(String jobId, String targetDirectory) throws Exception;


}
