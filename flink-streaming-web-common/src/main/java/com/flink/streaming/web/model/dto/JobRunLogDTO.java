package com.flink.streaming.web.model.dto;

import com.flink.streaming.web.model.entity.JobRunLog;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2020-08-17
 * @time 00:14
 */
@Data
public class JobRunLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

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
     * 运行后的任务id
     */
    private String jobId;

    /**
     * 远程日志url的地址
     */
    private String remoteLogUrl;

    /**
     * 启动时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 任务状态
     */
    private String jobStatus;


    private String creator;

    private String editor;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date editTime;


    /**
     * 启动时本地日志
     */
    private String localLog;

    private String runIp;


    public static JobRunLog toEntity(JobRunLogDTO jobRunLogDTO) {
        if (jobRunLogDTO == null) {
            return null;
        }
        JobRunLog jobRunLog = new JobRunLog();
        jobRunLog.setId(jobRunLogDTO.getId());
        jobRunLog.setJobConfigId(jobRunLogDTO.getJobConfigId());
        jobRunLog.setJobName(jobRunLogDTO.getJobName());
        jobRunLog.setDeployMode(jobRunLogDTO.getDeployMode());
        jobRunLog.setJobId(jobRunLogDTO.getJobId());
        jobRunLog.setRemoteLogUrl(jobRunLogDTO.getRemoteLogUrl());
        jobRunLog.setStartTime(jobRunLogDTO.getStartTime());
        jobRunLog.setEndTime(jobRunLogDTO.getEndTime());
        jobRunLog.setJobStatus(jobRunLogDTO.getJobStatus());
        jobRunLog.setCreator(jobRunLogDTO.getCreator());
        jobRunLog.setEditor(jobRunLogDTO.getEditor());
        jobRunLog.setCreateTime(jobRunLogDTO.getCreateTime());
        jobRunLog.setEditTime(jobRunLogDTO.getEditTime());
        jobRunLog.setLocalLog(jobRunLogDTO.getLocalLog());
        jobRunLog.setRunIp(jobRunLogDTO.getRunIp());
        return jobRunLog;
    }


    public static JobRunLogDTO toDTO(JobRunLog jobRunLog) {
        if (jobRunLog == null) {
            return null;
        }
        JobRunLogDTO jobRunLogDTO = new JobRunLogDTO();
        jobRunLogDTO.setId(jobRunLog.getId());
        jobRunLogDTO.setJobConfigId(jobRunLog.getJobConfigId());
        jobRunLogDTO.setJobName(jobRunLog.getJobName());
        jobRunLogDTO.setDeployMode(jobRunLog.getDeployMode());
        jobRunLogDTO.setJobId(jobRunLog.getJobId());
        jobRunLogDTO.setRemoteLogUrl(jobRunLog.getRemoteLogUrl());
        jobRunLogDTO.setStartTime(jobRunLog.getStartTime());
        jobRunLogDTO.setEndTime(jobRunLog.getEndTime());
        jobRunLogDTO.setJobStatus(jobRunLog.getJobStatus());
        jobRunLogDTO.setCreateTime(jobRunLog.getCreateTime());
        jobRunLogDTO.setEditTime(jobRunLog.getEditTime());
        jobRunLogDTO.setCreator(jobRunLog.getCreator());
        jobRunLogDTO.setEditor(jobRunLog.getEditor());
        jobRunLogDTO.setLocalLog(jobRunLog.getLocalLog());
        jobRunLogDTO.setRunIp(jobRunLog.getRunIp());
        return jobRunLogDTO;
    }

    public static List<JobRunLogDTO> toListDTO(List<JobRunLog> jobRunLogList) {
        if (CollectionUtils.isEmpty(jobRunLogList)) {
            return Collections.emptyList();
        }
        List<JobRunLogDTO> list = new ArrayList<>();

        for (JobRunLog jobRunLog : jobRunLogList) {
            list.add(JobRunLogDTO.toDTO(jobRunLog));
        }
        return list;


    }


}
