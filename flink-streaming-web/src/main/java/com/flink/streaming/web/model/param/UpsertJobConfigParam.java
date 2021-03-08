package com.flink.streaming.web.model.param;

import com.flink.streaming.web.enums.AlarmTypeEnum;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
     * 三方jar udf、 连接器 等jar如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String extJarPath;


    /**
     * @see com.flink.streaming.web.enums.AlarmTypeEnum
     */
    private String alarmTypes;


    /**
     * 1:开启 0: 关闭
     */
    private Integer isOpen = YN.N.getValue();

    /**
     * @see com.flink.streaming.web.enums.JobConfigStatus
     * 1:运行中 0: 停止中 -1:运行失败
     */
    private Integer stauts = JobConfigStatus.STOP.getCode();


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
        jobConfigDTO.setStatus(JobConfigStatus.getJobConfigStatus(upsertJobConfigParam.getStauts()));
        if (StringUtils.isNotEmpty(upsertJobConfigParam.getExtJarPath())) {
            jobConfigDTO.setExtJarPath(upsertJobConfigParam.getExtJarPath().trim());
        }
        if (StringUtils.isNotEmpty(upsertJobConfigParam.getAlarmTypes())) {
            List<AlarmTypeEnum> list = new ArrayList<>();
            String[] types = upsertJobConfigParam.getAlarmTypes().split(",");
            for (String code : types) {
                AlarmTypeEnum alarmTypeEnum = AlarmTypeEnum.getAlarmTypeEnum(Integer.valueOf(code));
                if (alarmTypeEnum != null) {
                    list.add(alarmTypeEnum);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                jobConfigDTO.setAlarmTypeEnumList(list);
            }
        }

        return jobConfigDTO;
    }


}
