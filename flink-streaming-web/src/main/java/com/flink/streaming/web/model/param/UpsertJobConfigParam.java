package com.flink.streaming.web.model.param;

import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 22:22
 */
@Data
public class UpsertJobConfigParam {

    private Long id;

    /**
     * 任务名称
     */
    private String jobName;


    /**
     *
     */
    private String deployMode;

    /**
     * flink运行配置
     */
    private String flinkRunConfig;

    /**
     * Checkpoint信息
     */
    private String flinkCheckpointConfig;


    /**
     * sql语句
     */
    private String flinkSql;


    /**
     * udf注册名称如
     * utc2local|com.streaming.flink.udf.UTC2Local 多个可用;分隔
     * utc2local代表组册的名称
     * com.streaming.flink.udf.UTC2Local代表类名
     *
     */
    private String udfRegisterName;

    /**
     * udf地址 如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String  udfJarPath;


    /**
     * 1:开启 0: 关闭
     */
    private Integer isOpen= YN.N.getValue();

    /**
     * @see  com.flink.streaming.web.enums.JobConfigStatus
     * 1:运行中 0: 停止中 -1:运行失败
     */
    private Integer stauts= JobConfigStatus.STOP.getCode();


    public static JobConfigDTO toDTO(UpsertJobConfigParam upsertJobConfigParam) {
        if (upsertJobConfigParam == null) {
            return null;
        }
        JobConfigDTO jobConfigDTO = new JobConfigDTO();
        jobConfigDTO.setId(upsertJobConfigParam.getId());
        jobConfigDTO.setDeployModeEnum(DeployModeEnum.getModel(upsertJobConfigParam.getDeployMode()));
        jobConfigDTO.setJobName(upsertJobConfigParam.getJobName());
        jobConfigDTO.setFlinkRunConfig(upsertJobConfigParam.getFlinkRunConfig());
        jobConfigDTO.setFlinkCheckpointConfig(upsertJobConfigParam.getFlinkCheckpointConfig());
        jobConfigDTO.setFlinkSql(upsertJobConfigParam.getFlinkSql());
        jobConfigDTO.setIsOpen(upsertJobConfigParam.getIsOpen());
        jobConfigDTO.setStauts(JobConfigStatus.getJobConfigStatus(upsertJobConfigParam.getStauts()) );
        if (StringUtils.isNotEmpty(upsertJobConfigParam.getUdfJarPath())){
            jobConfigDTO.setUdfJarPath(upsertJobConfigParam.getUdfJarPath().trim());
        }

        jobConfigDTO.setUdfRegisterName(upsertJobConfigParam.getUdfRegisterName());
        return jobConfigDTO;
    }



}
