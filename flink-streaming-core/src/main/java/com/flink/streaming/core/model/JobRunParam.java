package com.flink.streaming.core.model;

import com.flink.streaming.common.model.CheckPointParam;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-21
 * @time 02:10
 */
@Data
public class JobRunParam {
    /**
     * sql语句目录
     */
    private String sqlPath;


    /**
     * CheckPoint 参数
     */
    private CheckPointParam checkPointParam;


    /**
     * flink catalog type，目前仅支持hive catalog和默认memory catalog
     */
    private String catalog;


    /**
     * hive catalog的配置文件目录
     */
    private String hiveConfDir;

}
