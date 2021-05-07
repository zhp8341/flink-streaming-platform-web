package com.flink.streaming.web.enums;

import com.flink.streaming.web.model.vo.SysConfigVO;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 23:23
 */
@Getter
public enum SysConfigEnum {

    FLINK_HOME("flink_home",
            "flink客户端目录（必选）",
            SysConfigEnumType.SYS.name()),

    FLINK_STREAMING_PLATFORM_WEB_HOME("flink_streaming_platform_web_home",
            "flink-streaming-platform-web应用安装的目录（必选）",
            SysConfigEnumType.SYS.name()),


    YARN_RM_HTTP_ADDRESS("yarn_rm_http_address",
            "yarn的rm Http地址（yarn per 模式必须） ",
            SysConfigEnumType.SYS.name()),


    FLINK_REST_HTTP_ADDRESS("flink_rest_http_address",
            "flink Rest & web frontend 地址(Local Cluster模式)",
            SysConfigEnumType.SYS.name()),


    FLINK_REST_HA_HTTP_ADDRESS("flink_rest_ha_http_address",
            "flink Rest & web frontend HA 地址(Standalone Cluster模式 支持HA 可以填写多个地址 ;用分隔)",
            SysConfigEnumType.SYS.name()),


    DINGDING_ALARM_URL("dingding_alart_url",
            "钉钉告警所需的url（如果不填写将无法告警）",
            SysConfigEnumType.ALART.name()),


    CALLBACK_ALARM_URL("callback_alart_url",
            "自定义http回调告警(只需填写url即可如:http://127.0.0.1/alarmCallback 地址必须是alarmCallback )",
            SysConfigEnumType.ALART.name()),

    ;


    private String key;

    private String desc;

    private String type;

    SysConfigEnum(String key, String desc, String type) {
        this.key = key;
        this.desc = desc;
        this.type = type;
    }

    public static SysConfigEnum getSysConfigEnum(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        for (SysConfigEnum sysConfigEnum : SysConfigEnum.values()) {
            if (sysConfigEnum.getKey().equals(key.toLowerCase())) {
                return sysConfigEnum;
            }

        }
        return null;

    }

    public static String getType(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        for (SysConfigEnum sysConfigEnum : SysConfigEnum.values()) {
            if (sysConfigEnum.getKey().equals(key.toLowerCase())) {
                return sysConfigEnum.getType();
            }

        }
        return null;
    }

    public static List<SysConfigVO> getSysConfigEnumByType(String type) {
        if (StringUtils.isEmpty(type)) {
            return null;
        }
        List<SysConfigVO> list = new ArrayList<>();
        for (SysConfigEnum sysConfigEnum : SysConfigEnum.values()) {
            if (sysConfigEnum.getType().equals(type.toUpperCase())) {
                list.add(new SysConfigVO(sysConfigEnum.getKey(), sysConfigEnum.getDesc()));
            }
        }
        return list;
    }


    public static List<String> getMustKey() {
        List<String> list = new ArrayList<>();
        list.add(SysConfigEnum.FLINK_HOME.getKey());
        list.add(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey());
        return list;
    }

}
