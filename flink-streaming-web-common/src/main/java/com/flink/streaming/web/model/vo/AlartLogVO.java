package com.flink.streaming.web.model.vo;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.flink.streaming.web.enums.AlarmLogStatusEnum;
import com.flink.streaming.web.model.dto.AlartLogDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 23:34
 */
@Data
public class AlartLogVO {

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
    private String typeDesc;

    /**
     * 1:成功 0:失败
     */
    private String statusDesc;

    private Integer status;


    /**
     * 失败原因
     */
    private String failLog;


    /**
     * 创建时间
     */
    private String createTime;


    public static AlartLogVO toVO(AlartLogDTO alartLogDTO) {
        if (alartLogDTO == null) {
            return null;
        }
        AlartLogVO alartLogVO = new AlartLogVO();
        alartLogVO.setId(alartLogDTO.getId());
        alartLogVO.setJobConfigId(alartLogDTO.getJobConfigId());
        alartLogVO.setJobName(alartLogDTO.getJobName());
        alartLogVO.setMessage(alartLogDTO.getMessage());
        alartLogVO.setStatus(alartLogDTO.getAlarmLogStatusEnum().getCode());
        if (alartLogDTO.getAlarMLogTypeEnum() != null) {
            alartLogVO.setTypeDesc(alartLogDTO.getAlarMLogTypeEnum().getDesc());
        }
        if (alartLogDTO.getAlarmLogStatusEnum() != null) {
            if (AlarmLogStatusEnum.SUCCESS.equals(alartLogDTO.getAlarmLogStatusEnum())) {
                alartLogVO.setStatusDesc(alartLogDTO.getAlarmLogStatusEnum().getDesc());
            } else {
                alartLogVO.setStatusDesc("<span style=\"color: red\">" + alartLogDTO.getAlarmLogStatusEnum().getDesc() + "</span>");
            }

        }
        alartLogVO.setFailLog(alartLogDTO.getFailLog());
        alartLogVO.setCreateTime(DateUtil.format(alartLogDTO.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));

        return alartLogVO;
    }


    public static List<AlartLogVO> toListVO(List<AlartLogDTO> alartLogDTOList) {
        if (CollectionUtil.isEmpty(alartLogDTOList)) {
            return Collections.emptyList();
        }

        List<AlartLogVO> list = new ArrayList<>();
        for (AlartLogDTO alartLogDTO : alartLogDTOList) {
            AlartLogVO alartLogVO = AlartLogVO.toVO(alartLogDTO);
            if (alartLogVO != null) {
                list.add(alartLogVO);
            }
        }

        return list;

    }

}
