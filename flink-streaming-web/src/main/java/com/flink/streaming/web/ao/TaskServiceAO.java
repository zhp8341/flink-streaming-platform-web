package com.flink.streaming.web.ao;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-22
 * @time 19:59
 */
public interface TaskServiceAO {

    /**
     * 检查任务状态一致性问题（任务是不是挂掉）
     * <p>
     * 如：数据库里面任务状态是运行中，实际任务在yarn上已经挂掉啦
     *
     * @author zhuhuipei
     * @date 2020-09-22
     * @time 23:00
     */
    void checkJobStatus();


    /**
     * 针对在线运行任务进行自动SavePoint
     * @author zhuhuipei
     * @date 2020-09-22
     * @time 23:25
     */
    void autoSavePoint();

}
