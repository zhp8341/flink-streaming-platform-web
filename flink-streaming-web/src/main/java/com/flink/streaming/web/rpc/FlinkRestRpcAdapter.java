package com.flink.streaming.web.rpc;

import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.model.flink.JobRunRequestInfo;
import com.flink.streaming.web.rpc.model.JobStandaloneInfo;

import java.io.File;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 23:43
 */
public interface FlinkRestRpcAdapter {

    String uploadJarAndReturnJarId(File file, DeployModeEnum deployModeEnum);

    default String uploadJarAndReturnJarId(String filePath, DeployModeEnum deployModeEnum) {
        return uploadJarAndReturnJarId(new File(filePath), deployModeEnum);
    }

    String runJarByJarId(String jarId, JobRunRequestInfo info, DeployModeEnum deployModeEnum);


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
