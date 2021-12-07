package com.flink.streaming.web.model.dto;

import com.flink.streaming.web.enums.AlarmTypeEnum;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.common.enums.JobTypeEnum;
import com.flink.streaming.web.model.entity.JobConfig;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2020-07-10
 * @time 01:46
 */
@Data
public class JobConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * flink的模式
     */
    private DeployModeEnum deployModeEnum;

    /**
     * flink运行配置
     */
    private String flinkRunConfig;

    /**
     * checkpointConfig 配置
     */
    private String flinkCheckpointConfig;

    /**
     * flink运行配置
     */
    private String jobId;

    /**
     * 1:开启 0: 关闭
     */
    private Integer isOpen;

    /**
     * @see JobConfigStatus
     * 1:运行中 0: 停止中 -1:运行失败
     */
    private JobConfigStatus status;


    /**
     * 三方jar udf、 连接器 等jar如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String extJarPath;

    /**
     * 最后一次启动时间
     */
    private Date lastStartTime;

    /**
     * 更新版本号 用于乐观锁
     */
    private Integer version;

    /**
     * sql语句
     */
    private String flinkSql;


    /**
     * 任务类型
     */
    private JobTypeEnum jobTypeEnum;

    /**
     * 启动jar可能需要使用的自定义参数
     */
    private String customArgs;

    /**
     * 程序入口类
     */
    private String customMainClass;

    /**
     * 自定义jar的http地址 如:http://ccblog.cn/xx.jar
     */
    private String customJarUrl;


    private List<AlarmTypeEnum> alarmTypeEnumList;


    private Long lastRunLogId;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;

    private String creator;

    private String editor;

    public static JobConfig toEntity(JobConfigDTO jobConfigDTO) {
        if (jobConfigDTO == null) {
            return null;
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setId(jobConfigDTO.getId());
        jobConfig.setJobName(jobConfigDTO.getJobName());
        if (jobConfigDTO.getDeployModeEnum() != null) {
            jobConfig.setDeployMode(jobConfigDTO.getDeployModeEnum().name());
        }
        jobConfig.setFlinkRunConfig(jobConfigDTO.getFlinkRunConfig());
        jobConfig.setFlinkCheckpointConfig(jobConfigDTO.getFlinkCheckpointConfig());
        jobConfig.setJobId(jobConfigDTO.getJobId());
        jobConfig.setIsOpen(jobConfigDTO.getIsOpen());
        jobConfig.setStauts(jobConfigDTO.getStatus().getCode());
        jobConfig.setLastStartTime(jobConfigDTO.getLastStartTime());
        jobConfig.setVersion(jobConfigDTO.getVersion());
        jobConfig.setFlinkSql(jobConfigDTO.getFlinkSql());
        jobConfig.setCreateTime(jobConfigDTO.getCreateTime());
        jobConfig.setEditTime(jobConfigDTO.getEditTime());
        jobConfig.setCreator(jobConfigDTO.getCreator());
        jobConfig.setEditor(jobConfigDTO.getEditor());
        jobConfig.setLastRunLogId(jobConfigDTO.getLastRunLogId());
        jobConfig.setExtJarPath(jobConfigDTO.getExtJarPath());

        if (jobConfigDTO.getJobTypeEnum() != null) {
            jobConfig.setJobType(jobConfigDTO.getJobTypeEnum().getCode());
        }
        jobConfig.setCustomArgs(jobConfigDTO.getCustomArgs());
        jobConfig.setCustomMainClass(jobConfigDTO.getCustomMainClass());
        jobConfig.setCustomJarUrl(jobConfigDTO.getCustomJarUrl());

        return jobConfig;
    }


    public static JobConfigDTO toDTO(JobConfig jobConfig) {
        if (jobConfig == null) {
            return null;
        }
        JobConfigDTO jobConfigDTO = new JobConfigDTO();
        jobConfigDTO.setId(jobConfig.getId());
        jobConfigDTO.setJobName(jobConfig.getJobName());
        jobConfigDTO.setDeployModeEnum(DeployModeEnum.getModel(jobConfig.getDeployMode()));
        jobConfigDTO.setFlinkRunConfig(jobConfig.getFlinkRunConfig());
        jobConfigDTO.setFlinkCheckpointConfig(jobConfig.getFlinkCheckpointConfig());
        jobConfigDTO.setJobId(jobConfig.getJobId());
        jobConfigDTO.setIsOpen(jobConfig.getIsOpen());
        jobConfigDTO.setStatus(JobConfigStatus.getJobConfigStatus(jobConfig.getStauts()));
        jobConfigDTO.setLastStartTime(jobConfig.getLastStartTime());
        jobConfigDTO.setVersion(jobConfig.getVersion());
        jobConfigDTO.setCreateTime(jobConfig.getCreateTime());
        jobConfigDTO.setEditTime(jobConfig.getEditTime());
        jobConfigDTO.setCreator(jobConfig.getCreator());
        jobConfigDTO.setEditor(jobConfig.getEditor());
        jobConfigDTO.setFlinkSql(jobConfig.getFlinkSql());
        jobConfigDTO.setLastRunLogId(jobConfig.getLastRunLogId());
        jobConfigDTO.setExtJarPath(jobConfig.getExtJarPath());

        jobConfigDTO.setJobTypeEnum(JobTypeEnum.getJobTypeEnum(jobConfig.getJobType()));
        jobConfigDTO.setCustomArgs(jobConfig.getCustomArgs());
        jobConfigDTO.setCustomMainClass(jobConfig.getCustomMainClass());
        jobConfigDTO.setCustomJarUrl(jobConfig.getCustomJarUrl());

        return jobConfigDTO;
    }


    public static List<JobConfigDTO> toListDTO(List<JobConfig> jobConfigList) {
        if (CollectionUtils.isEmpty(jobConfigList)) {
            return Collections.emptyList();
        }

        List<JobConfigDTO> jobConfigDTOList = new ArrayList<JobConfigDTO>();

        for (JobConfig jobConfig : jobConfigList) {
            jobConfigDTOList.add(toDTO(jobConfig));
        }

        return jobConfigDTOList;
    }


    public static String buildRunName(String jobName) {

        return "flink@" + jobName;
    }


    public static JobConfigDTO bulidStop(Long id) {
        JobConfigDTO jobConfig = new JobConfigDTO();
        jobConfig.setStatus(JobConfigStatus.STOP);
        jobConfig.setEditor("sys_auto");
        jobConfig.setId(id);
        jobConfig.setJobId("");
        return jobConfig;
    }

}
