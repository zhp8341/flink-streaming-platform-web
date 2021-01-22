package com.flink.streaming.web.model.dto;

import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.enums.SysConfigEnum;
import lombok.Data;

import java.util.Map;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-17
 * @time 20:14
 */
@Data
public class JobRunParamDTO {

    /**
     * flink bin目录地址
     */
    private String flinkBinPath;

    /**
     * flink 运行参数 如：-yjm 1024m -ytm 2048m -yd -m yarn-cluster
     */
    private String flinkRunParam;

    /**
     * sql语句存放的目录
     */
    private String sqlPath;


    /**
     * checkpointConfig
     */
    private String flinkCheckpointConfig;

    /**
     * flink-streaming-platform-web 所在目录 如：/use/local/flink-streaming-platform-web
     */
    private String sysHome;

    /**
     * flink catalog type，目前仅支持hive catalog和默认memory catalog
     */
    private String flinkCatalogType;

    /**
     * hive catalog的配置文件目录
     */
    private String hiveCatalogConfDir;

    public JobRunParamDTO(String flinkBinPath,
                          String flinkRunParam,
                          String sqlPath,
                          String sysHome,
                          String flinkCheckpointConfig,
                          String flinkCatalogType,
                          String hiveCatalogConfDir) {
        this.flinkBinPath = flinkBinPath;
        this.flinkRunParam = flinkRunParam;
        this.sqlPath = sqlPath;
        this.sysHome = sysHome;
        this.flinkCheckpointConfig = flinkCheckpointConfig;
        this.flinkCatalogType = flinkCatalogType;
        this.hiveCatalogConfDir = hiveCatalogConfDir;
    }

    public static JobRunParamDTO getJobRunYarnDTO(Map<String, String> systemConfigMap, JobConfigDTO jobConfigDTO, String sqlPath) {

        String flinkBinPath = SystemConstants.buildFlinkBin(systemConfigMap.get(SysConfigEnum.FLINK_HOME.getKey()));

        String flinkRunParam = jobConfigDTO.getFlinkRunConfig();

        String sysHome = systemConfigMap.get(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey());

        String flinkCatalogType = systemConfigMap.getOrDefault(SysConfigEnum.FLINK_CATALOG_TYPE.getKey(), "memory");

        String hiveCatalogConfDir = systemConfigMap.get(SysConfigEnum.HIVE_CATALOG_CONF_DIR.getKey());

        JobRunParamDTO jobRunParamDTO = new JobRunParamDTO(
                flinkBinPath,
                flinkRunParam,
                sqlPath,
                sysHome,
                jobConfigDTO.getFlinkCheckpointConfig(),
                flinkCatalogType,
                hiveCatalogConfDir);

        return jobRunParamDTO;

    }
}
