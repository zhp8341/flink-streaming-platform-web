package com.flink.streaming.web.model.vo;

import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.enums.AlarmTypeEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-04
 * @time 01:28
 */
@Data
public class DetailJobConfigVO {

    private Long id;

    /**
     * 任务名称
     */
    private String jobName;


    /**
     * flink运行配置
     */
    private String jobId;


    private String deployMode;
    /**
     * 1:开启 0: 关闭
     */
    private Integer isOpen;


    private String openStr;


    private Integer stauts;


    private String stautsStr;


    private String lastStartTime;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String editTime;

    /**
     * flink运行配置
     */
    private String flinkRunConfig;

    /**
     * flink运行配置
     */
    private String flinkCheckpointConfig;


    /**
     * 三方jar udf、 连接器 等jar如http://xxx.xxx.com/flink-streaming-udf.jar
     */
    private String extJarPath;

    /**
     * sql语句
     */
    private String flinkSql;


    private List<Integer> types;


    public static DetailJobConfigVO toVO(JobConfigDTO jobConfigDTO) {
        if (jobConfigDTO == null) {
            return null;
        }
        DetailJobConfigVO detailJobConfigVO = new DetailJobConfigVO();
        detailJobConfigVO.setId(jobConfigDTO.getId());
        detailJobConfigVO.setJobName(jobConfigDTO.getJobName());
        detailJobConfigVO.setFlinkRunConfig(jobConfigDTO.getFlinkRunConfig());
        if (StringUtils.isNotEmpty(jobConfigDTO.getFlinkCheckpointConfig())) {
            detailJobConfigVO.setFlinkCheckpointConfig(jobConfigDTO.getFlinkCheckpointConfig().replaceAll("\"", "&quot;"));
        }
        detailJobConfigVO.setJobId(jobConfigDTO.getJobId());
        detailJobConfigVO.setIsOpen(jobConfigDTO.getIsOpen());
        detailJobConfigVO.setOpenStr(YN.getYNByValue(jobConfigDTO.getIsOpen()).getDescribe());
        detailJobConfigVO.setStautsStr(jobConfigDTO.getStatus().getDesc());
        detailJobConfigVO.setStauts(jobConfigDTO.getStatus().getCode());
        detailJobConfigVO.setLastStartTime(DateFormatUtils.toFormatString(jobConfigDTO.getLastStartTime()));
        detailJobConfigVO.setCreateTime(DateFormatUtils.toFormatString(jobConfigDTO.getCreateTime()));
        detailJobConfigVO.setEditTime(DateFormatUtils.toFormatString(jobConfigDTO.getEditTime()));
        detailJobConfigVO.setFlinkSql(jobConfigDTO.getFlinkSql());
        detailJobConfigVO.setDeployMode(jobConfigDTO.getDeployModeEnum().name());
        detailJobConfigVO.setExtJarPath(jobConfigDTO.getExtJarPath());
        if (CollectionUtils.isNotEmpty(jobConfigDTO.getAlarmTypeEnumList())) {

            List<AlarmTypeEnum> alarmTypeEnumList = jobConfigDTO.getAlarmTypeEnumList();
            List<Integer> types = Lists.newArrayList();
            for (AlarmTypeEnum alarmTypeEnum : alarmTypeEnumList) {
                types.add(alarmTypeEnum.getCode());
            }
            detailJobConfigVO.setTypes(types);
        }

        return detailJobConfigVO;
    }


}
