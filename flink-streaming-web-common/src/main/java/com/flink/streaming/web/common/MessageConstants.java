package com.flink.streaming.web.common;

/**
 * 系统常量
 *
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 01:33
 */
public class MessageConstants {

    public static final String MESSAGE_001 = "任务启动中！ 请稍后刷新查看日志。。。";

    public static final String MESSAGE_002 = "任务正在运行中，请先停止任务。。。";

    public static final String MESSAGE_003 = "任务正在启动中 请稍等..。。。";

    public static final String MESSAGE_004 = "没有配置Checkpoint 不能能执行savepoint {}";

    public static final String MESSAGE_005 = "任务没有运行不能执行savepoint {}";

    public static final String MESSAGE_006 = "自定义jar任务不支持savePoint  任务:{}";

    public static final String MESSAGE_007 = "yarn集群上没有找到对应任务  任务:{}";

    public static final String MESSAGE_008 = "执行savePoint失败了 详见错误日志  任务:{}";

    public static final String MESSAGE_009 = "没有获取到savepointPath路径目录  任务:{}";

    public static final String MESSAGE_010 = "无法获取 user.name";

}
