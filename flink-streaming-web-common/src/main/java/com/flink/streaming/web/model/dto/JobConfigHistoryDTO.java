package com.flink.streaming.web.model.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.flink.streaming.web.model.entity.JobConfig;
import com.flink.streaming.web.model.entity.JobConfigHistory;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2021/5/5
 * @time 19:49
 */
@Data
public class JobConfigHistoryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * job_config主表Id
     */
    private Long jobConfigId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 提交模式: standalone 、yarn 、yarn-session
     */
    private String deployMode;

    /**
     * flink运行配置
     */
    private String flinkRunConfig;

    /**
     * checkPoint配置
     */
    private String flinkCheckpointConfig;

    /**
     * udf地址及连接器jar 如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String extJarPath;

    /**
     * 更新版本号
     */
    private Integer version;

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

    /**
     * sql语句
     */
    private String flinkSql;


    public static JobConfigHistory toEntity(JobConfigHistoryDTO jobConfigHistoryDTO) {
        if (jobConfigHistoryDTO == null) {
            return null;
        }
        JobConfigHistory jobConfigHistory = new JobConfigHistory();
        jobConfigHistory.setId(jobConfigHistoryDTO.getId());
        jobConfigHistory.setJobConfigId(jobConfigHistoryDTO.getJobConfigId());
        jobConfigHistory.setJobName(jobConfigHistoryDTO.getJobName());
        jobConfigHistory.setDeployMode(jobConfigHistoryDTO.getDeployMode());
        jobConfigHistory.setFlinkRunConfig(jobConfigHistoryDTO.getFlinkRunConfig());
        jobConfigHistory.setFlinkCheckpointConfig(jobConfigHistoryDTO.getFlinkCheckpointConfig());
        jobConfigHistory.setExtJarPath(jobConfigHistoryDTO.getExtJarPath());
        jobConfigHistory.setVersion(jobConfigHistoryDTO.getVersion());
        jobConfigHistory.setCreateTime(jobConfigHistoryDTO.getCreateTime());
        jobConfigHistory.setEditTime(jobConfigHistoryDTO.getEditTime());
        jobConfigHistory.setCreator(jobConfigHistoryDTO.getCreator());
        jobConfigHistory.setEditor(jobConfigHistoryDTO.getEditor());
        jobConfigHistory.setFlinkSql(jobConfigHistoryDTO.getFlinkSql());
        return jobConfigHistory;
    }


    public static JobConfigHistoryDTO toDTO(JobConfigHistory jobConfigHistory) {
        if (jobConfigHistory == null) {
            return null;
        }
        JobConfigHistoryDTO jobConfigHistoryDTO = new JobConfigHistoryDTO();
        jobConfigHistoryDTO.setId(jobConfigHistory.getId());
        jobConfigHistoryDTO.setJobConfigId(jobConfigHistory.getJobConfigId());
        jobConfigHistoryDTO.setJobName(jobConfigHistory.getJobName());
        jobConfigHistoryDTO.setDeployMode(jobConfigHistory.getDeployMode());
        jobConfigHistoryDTO.setFlinkRunConfig(jobConfigHistory.getFlinkRunConfig());
        jobConfigHistoryDTO.setFlinkCheckpointConfig(jobConfigHistory.getFlinkCheckpointConfig());
        jobConfigHistoryDTO.setExtJarPath(jobConfigHistory.getExtJarPath());
        jobConfigHistoryDTO.setVersion(jobConfigHistory.getVersion());
        jobConfigHistoryDTO.setCreateTime(jobConfigHistory.getCreateTime());
        jobConfigHistoryDTO.setEditTime(jobConfigHistory.getEditTime());
        jobConfigHistoryDTO.setCreator(jobConfigHistory.getCreator());
        jobConfigHistoryDTO.setEditor(jobConfigHistory.getEditor());
        jobConfigHistoryDTO.setFlinkSql(jobConfigHistory.getFlinkSql());
        return jobConfigHistoryDTO;
    }

    public static List<JobConfigHistoryDTO> toListDTO(List<JobConfigHistory> jobConfigHistoryList) {
        if (CollectionUtil.isEmpty(jobConfigHistoryList)) {
            return Collections.EMPTY_LIST;
        }

        List<JobConfigHistoryDTO> list = Lists.newArrayList();

        for (JobConfigHistory jobConfigHistory : jobConfigHistoryList) {

            JobConfigHistoryDTO jobConfigHistoryDTO = JobConfigHistoryDTO.toDTO(jobConfigHistory);
            if (jobConfigHistoryDTO != null) {
                list.add(jobConfigHistoryDTO);
            }
        }

        return list;
    }


    public static JobConfigHistoryDTO to(JobConfig jobConfig) {
        if (jobConfig == null) {
            return null;
        }
        JobConfigHistoryDTO jobConfigHistoryDTO = new JobConfigHistoryDTO();
        jobConfigHistoryDTO.setJobConfigId (jobConfig.getId());
        jobConfigHistoryDTO.setJobName(jobConfig.getJobName());
        jobConfigHistoryDTO.setDeployMode(jobConfig.getDeployMode());
        jobConfigHistoryDTO.setFlinkRunConfig(jobConfig.getFlinkRunConfig());
        jobConfigHistoryDTO.setFlinkCheckpointConfig(jobConfig.getFlinkCheckpointConfig());
        jobConfigHistoryDTO.setExtJarPath(jobConfig.getExtJarPath());
        jobConfigHistoryDTO.setVersion(jobConfig.getVersion());
        jobConfigHistoryDTO.setCreateTime(jobConfig.getCreateTime());
        jobConfigHistoryDTO.setEditTime(jobConfig.getEditTime());
        jobConfigHistoryDTO.setCreator(jobConfig.getCreator());
        jobConfigHistoryDTO.setEditor(jobConfig.getEditor());
        jobConfigHistoryDTO.setFlinkSql(jobConfig.getFlinkSql());
        return jobConfigHistoryDTO;
    }
}
