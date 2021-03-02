package com.flink.streaming.web.enums;

import lombok.Getter;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-13
 * @time 19:12
 */
@Getter
public enum SysErrorEnum {


    SYSTEM_ERROR("500", "系统异常"),
    CUSTOMER_SYSTEM_ERROR("501", "自定义异常"),
    BODY_NOT_MATCH("400", "请求的数据格式不符!"),
    SIGNATURE_NOT_MATCH("401", "请求的数字签名不匹配!"),
    NOT_FOUND("404", "未找到该资源!"),
    INTERNAL_SERVER_ERROR("501", "服务器内部错误!"),
    SERVER_BUSY("503", "服务器正忙，请稍后再试!"),

    PARAM_IS_NULL("100", "参数为空"),
    PARAM_IS_NULL_YARN_APPID("101", "appid参数为空"),
    HTTP_REQUEST_IS_NULL("102", "appid参数为空"),
    START_JOB_FAIL("103", "开启任务失败"),
    STOP_JOB_FAIL("104", "关闭任务失败"),


    USER_IS_NOT_NULL("10000", "帐号不存在"),
    USER_PASSWORD_ERROR("10001", "帐号密码错误"),
    USER_PASSWORD_EXPIRED("10002", "密码已经被修改"),
    USER_IS_EXIST("10003", "帐号已存在"),
    USER_IS_STOP("10004", "帐号已停用"),

    JOB_CONFIG_PARAM_IS_NULL("20000", "参数不能为空"),
    JOB_CONFIG_JOB_NAME_IS_EXIST("20001", "任务名称已经存在"),
    JOB_CONFIG_JOB_IS_NOT_EXIST("20002", "任务配置不存在"),
    JOB_CONFIG_JOB_IS_OPEN("20003", "任务开启或者运行状态不能修改，请先关闭任务"),
    JOB_CONFIG_DEPLOY_MODE_ENUM_NULL("20004", "DeployModeEnum 参数为空"),

    SYSTEM_CONFIG_IS_NULL("30000", "请先配置系统环境 "),
    SYSTEM_CONFIG_IS_NULL_FLINK_HOME("30001", "请先flink_home环境的目录地址"),
    SYSTEM_CONFIG_IS_NULL_YARN_RM_HTTP_ADDRESS("30002", "请先配置yarn的http地址"),
    SYSTEM_CONFIG_IS_NULL_FLINK_REST_HTTP_ADDRESS("30004", "请先配置flink rest 的http地址"),
    SYSTEM_CONFIG_IS_NULL_FLINK_REST_HA_HTTP_ADDRESS("30005", "请先配置flink rest  ha 的http地址"),
    SYSTEM_CONFIG_IS_NULL_FLINK_STREAMING_PLATFORM_WEB_HOME("30003", "请先配置web平台的目录地址"),

    YARN_CODE("00000", "yarn队列中没有找到运行的任务"),


    ADD_SAVEPOINT_ERROR("40000", "手动添加SAVEPOINT失败"),


    ALARM_DINGDING_NULL("50000","请到报警管理-报警设置 配置钉钉告警url"),

    ALARM_HTTP_NULL("50001","请到报警管理-报警设置 配置回调url"),

    ;


    private String code;

    private String errorMsg;

    SysErrorEnum(String code, String errorMsg) {
        this.code = code;
        this.errorMsg = errorMsg;
    }
}
