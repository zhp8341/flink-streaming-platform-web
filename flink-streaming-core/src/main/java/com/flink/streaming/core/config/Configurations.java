package com.flink.streaming.core.config;

import com.flink.streaming.common.model.SqlConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.TableEnvironment;

import java.util.Map;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/1/17
 * @time 23:57
 */
@Slf4j
public class Configurations {

    /**
     * 设置Configuration
     *
     * @author zhuhuipei
     * @date 2020-06-23
     * @time 00:46
     */
    public static void setConfiguration(TableEnvironment tEnv, SqlConfig sqlConfig) {
        if (sqlConfig == null || MapUtils.isEmpty(sqlConfig.getMapConfig())) {
            return;
        }
        Configuration configuration = tEnv.getConfig().getConfiguration();
        for (Map.Entry<String, String> entry : sqlConfig.getMapConfig().entrySet()) {
            log.info("#############setConfiguration#############\n  {} {}", entry.getKey(), entry.getValue());
            configuration.setString(entry.getKey(), entry.getValue());
        }
    }


}
