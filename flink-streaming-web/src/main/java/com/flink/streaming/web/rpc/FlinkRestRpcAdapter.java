package com.flink.streaming.web.rpc;

import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.rpc.model.JobStandaloneInfo;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 23:43
 */
public interface FlinkRestRpcAdapter {


    /**
     * Standalone 模式下获取状态
     *
     * @author zhuhuipei
     * @date 2020/11/3
     * @time 23:47
     */
    JobStandaloneInfo getJobInfoForStandaloneByAppId(String appId, DeployModeEnum deployModeEnum);


    /**
     * 基于flink rest API取消任务
     *
     * @author zhuhuipei
     * @date 2020/11/3
     * @time 22:50
     */
    void cancelJobForFlinkByAppId(String jobId, DeployModeEnum deployModeEnum);


    /**
     * 获取savepoint路径
     *
     * @author zhuhuipei
     * @date 2021/3/31
     * @time 22:01
     */
    String savepointPath(String jobId, DeployModeEnum deployModeEnum);


}
