package com.flink.streaming.web.adapter;

import com.flink.streaming.web.enums.YarnStateEnum;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 20:15
 */
public interface HttpRequestAdapter {

    /**
     * 通过任务名称获取yarn 的appId
     *
     * @author zhuhuipei
     * @date 2020-08-06
     * @time 20:18
     */
    String getAppIdByYarn(String jobName, String queueName);

    /**
     * 通过http杀掉一个任务
     *
     * @author zhuhuipei
     * @date 2020-08-06
     * @time 20:50
     */
    void stopJobByJobId(String appId);


    /**
     * 查询yarn 上某任务状态
     *
     * @author zhuhuipei
     * @date 2020-08-07
     * @time 21:36
     */
    YarnStateEnum getJobStateByJobId(String appId);


}
