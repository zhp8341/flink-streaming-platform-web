package com.flink.streaming.web.model.dto;

import cn.hutool.core.collection.CollectionUtil;
import com.flink.streaming.web.enums.AlarmLogStatusEnum;
import com.flink.streaming.web.enums.AlarmLogTypeEnum;
import com.flink.streaming.web.model.entity.AlartLog;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author
 */
@Data
public class AlartLogDTO {

    private Long id;

    private Long jobConfigId;


    private String jobName;


    /**
     * 消息内容
     */
    private String message;

    /**
     * 1:钉钉
     */
    private AlarmLogTypeEnum alarMLogTypeEnum;

    /**
     * 1:成功 0:失败
     */
    private AlarmLogStatusEnum alarmLogStatusEnum;


    /**
     * 失败原因
     */
    private String failLog;


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


    public static AlartLog toEntity(AlartLogDTO alartLogDTO) {
        if (alartLogDTO == null) {
            return null;
        }
        AlartLog alartLog = new AlartLog();
        alartLog.setId(alartLogDTO.getId());
        alartLog.setJobConfigId(alartLogDTO.getJobConfigId());
        alartLog.setMessage(alartLogDTO.getMessage());
        alartLog.setType(alartLogDTO.getAlarMLogTypeEnum().getCode());
        alartLog.setStatus(alartLogDTO.getAlarmLogStatusEnum().getCode());
        alartLog.setFailLog(alartLogDTO.getFailLog());
        alartLog.setCreateTime(alartLogDTO.getCreateTime());
        alartLog.setEditTime(alartLogDTO.getEditTime());
        alartLog.setCreator(alartLogDTO.getCreator());
        alartLog.setEditor(alartLogDTO.getEditor());
        alartLog.setJobName(alartLogDTO.getJobName());
        return alartLog;
    }

    public static AlartLogDTO toDTO(AlartLog alartLog) {
        if (alartLog == null) {
            return null;
        }
        AlartLogDTO alartLogDTO = new AlartLogDTO();
        alartLogDTO.setId(alartLog.getId());
        alartLogDTO.setJobConfigId(alartLog.getJobConfigId());
        alartLogDTO.setMessage(alartLog.getMessage());
        alartLogDTO.setAlarMLogTypeEnum(AlarmLogTypeEnum.getAlarmLogTypeEnum(alartLog.getType()));
        alartLogDTO.setAlarmLogStatusEnum(AlarmLogStatusEnum.getAlarmLogStatusEnum(alartLog.getStatus()));
        alartLogDTO.setFailLog(alartLog.getFailLog());
        alartLogDTO.setCreateTime(alartLog.getCreateTime());
        alartLogDTO.setEditTime(alartLog.getEditTime());
        alartLogDTO.setCreator(alartLog.getCreator());
        alartLogDTO.setEditor(alartLog.getEditor());
        alartLogDTO.setJobName(alartLog.getJobName());
        return alartLogDTO;
    }

    public static List<AlartLogDTO> toListDTO(List<AlartLog> alartLogList) {
        if (CollectionUtil.isEmpty(alartLogList)) {
            return Collections.emptyList();
        }
        List<AlartLogDTO> list = new ArrayList<>();
        for (AlartLog alartLog : alartLogList) {
            AlartLogDTO alartLogDTO = AlartLogDTO.toDTO(alartLog);
            if (alartLog != null) {
                list.add(alartLogDTO);
            }
        }
        return list;
    }


}
