package com.flink.streaming.web.model.vo;

import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.enums.JobStatusEnum;
import com.flink.streaming.web.model.dto.JobRunLogDTO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
public class JobRunLogVO implements Serializable {

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

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String editTime;


    /**
     * 启动时本地日志
     */
    private String localLog;

    /**
     * 本地客户端日志
     */
    private String clinetJobUrl;


    public static JobRunLogVO toVO(JobRunLogDTO jobRunLogDTO, boolean isLocalLog,Integer port) {
        if (jobRunLogDTO == null) {
            return null;
        }
        JobRunLogVO jobRunLogVO = new JobRunLogVO();
        jobRunLogVO.setId(jobRunLogDTO.getId());
        jobRunLogVO.setJobConfigId(jobRunLogDTO.getJobConfigId());
        jobRunLogVO.setJobName(jobRunLogDTO.getJobName());
        jobRunLogVO.setDeployMode(jobRunLogDTO.getDeployMode());
        jobRunLogVO.setJobId(jobRunLogDTO.getJobId());
        jobRunLogVO.setRemoteLogUrl(jobRunLogDTO.getRemoteLogUrl());
        jobRunLogVO.setStartTime(jobRunLogDTO.getStartTime());
        jobRunLogVO.setEndTime(jobRunLogDTO.getEndTime());
        jobRunLogVO.setJobStatus(JobStatusEnum.getJobStatusEnum(jobRunLogDTO.getJobStatus()).getDesc());
        jobRunLogVO.setCreateTime(DateFormatUtils.toFormatString(jobRunLogDTO.getCreateTime()));
        jobRunLogVO.setEditTime(DateFormatUtils.toFormatString(jobRunLogDTO.getEditTime()));
        if (isLocalLog) {
            jobRunLogVO.setLocalLog(jobRunLogDTO.getLocalLog());
        }
        if (port!=null && StringUtils.isNotEmpty(jobRunLogDTO.getRunIp())){
            jobRunLogVO.setClinetJobUrl(String.format("http://%s:%s/log/getFlinkLocalJobLog",
                    jobRunLogDTO.getRunIp(),port));
        }

        return jobRunLogVO;
    }

    public static List<JobRunLogVO> toListVO(List<JobRunLogDTO> jobRunLogList, boolean isLocalLog) {
        if (CollectionUtils.isEmpty(jobRunLogList)) {
            return Collections.emptyList();
        }
        List<JobRunLogVO> list = new ArrayList<>();

        for (JobRunLogDTO jobRunLog : jobRunLogList) {
            list.add(JobRunLogVO.toVO(jobRunLog, isLocalLog,null));
        }
        return list;


    }


}
