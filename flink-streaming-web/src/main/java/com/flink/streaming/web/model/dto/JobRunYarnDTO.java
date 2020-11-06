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
public class JobRunYarnDTO {

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

    public JobRunYarnDTO(String flinkBinPath, String flinkRunParam, String sqlPath,  String sysHome,String flinkCheckpointConfig) {
        this.flinkBinPath = flinkBinPath;
        this.flinkRunParam = flinkRunParam;
        this.sqlPath = sqlPath;
        this.sysHome = sysHome;
        this.flinkCheckpointConfig = flinkCheckpointConfig;
    }

    public static JobRunYarnDTO getJobRunYarnDTO(Map<String, String> systemConfigMap, JobConfigDTO jobConfigDTO, String sqlPath) {

        String flinkBinPath = SystemConstants.buildFlinkBin(systemConfigMap.get(SysConfigEnum.FLINK_HOME.getKey()));

        String flinkRunParam = jobConfigDTO.getFlinkRunConfig();

        String sysHome = systemConfigMap.get(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey());

        JobRunYarnDTO jobRunYarnDTO = new JobRunYarnDTO(flinkBinPath, flinkRunParam, sqlPath, sysHome,jobConfigDTO.getFlinkCheckpointConfig());

        return jobRunYarnDTO;

    }
}
