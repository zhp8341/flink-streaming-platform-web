package com.flink.streaming.web.model.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.model.dto.JobConfigHistoryDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @date 2021/5/5
 * @time 19:49
 */
@Data
public class JobConfigHistoryVO implements Serializable {

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
    private String createTime;

    /**
     * 修改时间
     */
    private String editTime;

    private String creator;

    private String editor;

    /**
     * sql语句
     */
    private String flinkSql;


    public static JobConfigHistoryVO toVO(JobConfigHistoryDTO jobConfigHistoryDTO, boolean isFlinkSql) {
        if (jobConfigHistoryDTO == null) {
            return null;
        }
        JobConfigHistoryVO jobConfigHistoryVO = new JobConfigHistoryVO();
        jobConfigHistoryVO.setId(jobConfigHistoryDTO.getId());
        jobConfigHistoryVO.setJobConfigId(jobConfigHistoryDTO.getJobConfigId());
        jobConfigHistoryVO.setJobName(jobConfigHistoryDTO.getJobName());
        jobConfigHistoryVO.setDeployMode(jobConfigHistoryDTO.getDeployMode());
        jobConfigHistoryVO.setFlinkRunConfig(jobConfigHistoryDTO.getFlinkRunConfig());
        jobConfigHistoryVO.setFlinkCheckpointConfig(jobConfigHistoryDTO.getFlinkCheckpointConfig());
        jobConfigHistoryVO.setExtJarPath(jobConfigHistoryDTO.getExtJarPath());
        jobConfigHistoryVO.setVersion(jobConfigHistoryDTO.getVersion());
        jobConfigHistoryVO.setCreateTime(DateFormatUtils.toFormatString(jobConfigHistoryDTO.getCreateTime()));
        jobConfigHistoryVO.setEditTime(DateFormatUtils.toFormatString(jobConfigHistoryDTO.getEditTime()));
        jobConfigHistoryVO.setCreator(jobConfigHistoryDTO.getCreator());
        jobConfigHistoryVO.setEditor(jobConfigHistoryDTO.getEditor());
        if (isFlinkSql) {
            jobConfigHistoryVO.setFlinkSql(jobConfigHistoryDTO.getFlinkSql());
        }
        return jobConfigHistoryVO;
    }

    public static List<JobConfigHistoryVO> toListVO(List<JobConfigHistoryDTO> jobConfigHistoryDTOList) {
        if (CollectionUtil.isEmpty(jobConfigHistoryDTOList)) {
            return Collections.EMPTY_LIST;
        }
        List<JobConfigHistoryVO> list = new ArrayList<>();

        for (JobConfigHistoryDTO jobConfigHistoryDTO : jobConfigHistoryDTOList) {
            list.add(toVO(jobConfigHistoryDTO, Boolean.FALSE));
        }

        return list;
    }
}
