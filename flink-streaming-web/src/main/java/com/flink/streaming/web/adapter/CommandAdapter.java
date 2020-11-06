package com.flink.streaming.web.adapter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 20:09
 */
public interface CommandAdapter {

    /**
     * yarn per模式启动任务
     *
     * @author zhuhuipei
     * @date 2020-09-18
     * @time 20:18
     */
    void startForPerYarn(String command, StringBuilder localLog, Long jobRunLogId) throws Exception;


    /**
     *启动本地模式
     * @author zhuhuipei
     * @date 2020/11/1
     * @time 10:15
     */
    String startForLocal(String command, StringBuilder localLog, Long jobRunLogId) throws Exception;

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


}
