package com.flink.streaming.web.adapter;

import com.flink.streaming.web.model.flink.JobStandaloneInfo;
import com.flink.streaming.web.model.flink.JobYarnInfo;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 23:43
 */
public interface FlinkHttpRequestAdapter {

    /**
     * per yarn 模式下获取任务新状态
     *
     * @author zhuhuipei
     * @date 2020-09-18
     * @time 01:15
     */
    JobYarnInfo getJobInfoForPerYarnByAppId(String appId);


    /**
     * Standalone 模式下获取状态
     *
     * @author zhuhuipei
     * @date 2020/11/3
     * @time 23:47
     */
    JobStandaloneInfo getJobInfoForStandaloneByAppId(String appId);


    /**
     * per yarn 模式下 取消任务
     *
     * @param appId (yarn上的appId)
     * @param jobId (flink上的id)
     * @author zhuhuipei
     * @date 2020-09-18
     * @time 00:01
     */
    void cancelJobForYarnByAppId(String appId, String jobId);


    /**
     * 基于flink rest API取消任务
     *
     * @author zhuhuipei
     * @date 2020/11/3
     * @time 22:50
     */
    void cancelJobForFlinkByAppId(String jobId);


    /**
     * per yarn 模式下 获取SavepointPath 地址
     * <p>
     * 通过checkpoint 接口获取Savepoint地址
     *
     * @author zhuhuipei
     * @date 2020-09-21
     * @time 02:44
     */
    String getSavepointPath(String appId, String jobId);


}
