package com.flink.streaming.web.model.dto;

import com.flink.streaming.web.model.entity.JobAlarmConfig;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2021/2/27
 * @time 17:09
 */
@Data
public class JobAlarmConfigDTO {

    private Long id;

    /**
     * job_config主表id
     */
    private Long jobId;

    /**
     * 类型 1:钉钉告警 2:url回调 3:异常自动拉起任务
     */
    private Integer type;


    public static JobAlarmConfigDTO toDTO(JobAlarmConfig jobAlarmConfig) {
        if (jobAlarmConfig == null) {
            return null;
        }
        JobAlarmConfigDTO jobAlarmConfigDTO = new JobAlarmConfigDTO();
        jobAlarmConfigDTO.setId(jobAlarmConfig.getId());
        jobAlarmConfigDTO.setJobId(jobAlarmConfig.getJobId());
        jobAlarmConfigDTO.setType(jobAlarmConfig.getType());
        return jobAlarmConfigDTO;
    }

    public static JobAlarmConfig toEntity(JobAlarmConfigDTO jobAlarmConfigDTO) {
        if (jobAlarmConfigDTO == null) {
            return null;
        }
        JobAlarmConfig jobAlarmConfig = new JobAlarmConfig();
        jobAlarmConfig.setId(jobAlarmConfigDTO.getId());
        jobAlarmConfig.setJobId(jobAlarmConfigDTO.getJobId());
        jobAlarmConfig.setType(jobAlarmConfigDTO.getType());
        return jobAlarmConfig;
    }

    public static List<JobAlarmConfig> toEntityList(List<JobAlarmConfigDTO> jobAlarmConfigDTOList) {
        if (CollectionUtils.isEmpty(jobAlarmConfigDTOList)) {
            return Collections.emptyList();
        }
        List<JobAlarmConfig> list = new ArrayList<>();

        for (JobAlarmConfigDTO jobAlarmConfigDTO : jobAlarmConfigDTOList) {
            list.add(toEntity(jobAlarmConfigDTO));
        }
        return list;
    }


    public static List<JobAlarmConfigDTO> toListDTO(List<JobAlarmConfig> jobAlarmConfigList) {
        if (CollectionUtils.isEmpty(jobAlarmConfigList)) {
            return Collections.emptyList();
        }
        List<JobAlarmConfigDTO> list = new ArrayList<>();

        for (JobAlarmConfig jobAlarmConfig : jobAlarmConfigList) {
            list.add(toDTO(jobAlarmConfig));
        }
        return list;

    }


}
