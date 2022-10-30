package com.flink.streaming.web.model.vo;

import cn.hutool.core.collection.CollectionUtil;
import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import com.flink.streaming.web.common.util.DateFormatUtils;
import com.flink.streaming.web.common.util.HttpServiceCheckerUtil;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.enums.AlarmTypeEnum;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-04
 * @time 01:28
 */
@Data
public class JobConfigVO {

  private Long id;

  /**
   * 任务名称
   */
  private String jobName;

  private String deployMode;


  /**
   * flink运行配置
   */
  private String jobId;


  /**
   * 1:开启 0: 关闭
   */
  private Integer isOpen;


  private String isOpenStr;


  private Integer status;


  private String statusStr;


  private String lastStartTime;

  private Long lastRunLogId;

  private String flinkRunUrl;

  /**
   * 创建时间
   */
  private String createTime;

  /**
   * 修改时间
   */
  private String editTime;


  private String alarmStrs;

  /**
   * cron表达式
   */
  private String cron;

  public static JobConfigVO toVO(JobConfigDTO jobConfigDTO, Map<DeployModeEnum, String> map) {
    if (jobConfigDTO == null) {
      return null;
    }
    JobConfigVO jobConfigVO = new JobConfigVO();
    jobConfigVO.setId(jobConfigDTO.getId());
    jobConfigVO.setJobName(jobConfigDTO.getJobName());
    jobConfigVO.setJobId(jobConfigDTO.getJobId());
    jobConfigVO.setIsOpen(jobConfigDTO.getIsOpen());
    jobConfigVO.setIsOpenStr(YN.getYNByValue(jobConfigDTO.getIsOpen()).getDescribe());
    jobConfigVO.setStatus(jobConfigDTO.getStatus().getCode());
    jobConfigVO.setStatusStr(jobConfigDTO.getStatus().getDesc());
    jobConfigVO.setCron(jobConfigDTO.getCron());
    if (jobConfigDTO.getDeployModeEnum() != null) {
      jobConfigVO.setDeployMode(jobConfigDTO.getDeployModeEnum().name());
    }

    String domain = map.get(jobConfigDTO.getDeployModeEnum());

    if (StringUtils.isNotEmpty(domain)) {
      if ((DeployModeEnum.YARN_PER.equals(jobConfigDTO.getDeployModeEnum())
          || DeployModeEnum.YARN_APPLICATION.equals(jobConfigDTO.getDeployModeEnum()))
          && !StringUtils.isEmpty(jobConfigDTO.getJobId())) {
        jobConfigVO.setFlinkRunUrl(HttpUtil.buildUrl(domain,
            FlinkYarnRestUriConstants.getUriOverviewForYarn(jobConfigDTO.getJobId())));
      }
      if (DeployModeEnum.LOCAL.equals(jobConfigDTO.getDeployModeEnum())
          && !StringUtils.isEmpty(jobConfigDTO.getJobId())) {
        jobConfigVO
            .setFlinkRunUrl(domain + String.format(FlinkYarnRestUriConstants.URI_YARN_JOB_OVERVIEW,
                jobConfigDTO.getJobId()));
      }
      if (DeployModeEnum.STANDALONE.equals(jobConfigDTO.getDeployModeEnum())
          && !StringUtils.isEmpty(jobConfigDTO.getJobId())) {
        String[] urls = domain.split(SystemConstant.SEMICOLON);
        for (String url : urls) {
          if (HttpServiceCheckerUtil.checkUrlConnect(url)) {
            jobConfigVO.setFlinkRunUrl(url.trim()
                + String.format(FlinkYarnRestUriConstants.URI_YARN_JOB_OVERVIEW,
                jobConfigDTO.getJobId()));
            break;
          }
        }
      }
    }

    jobConfigVO.setLastRunLogId(jobConfigDTO.getLastRunLogId());
    jobConfigVO.setLastStartTime(DateFormatUtils.toFormatString(jobConfigDTO.getLastStartTime()));
    jobConfigVO.setCreateTime(DateFormatUtils.toFormatString(jobConfigDTO.getCreateTime()));
    jobConfigVO.setEditTime(DateFormatUtils.toFormatString(jobConfigDTO.getEditTime()));
    return jobConfigVO;
  }


  public static List<JobConfigVO> toListVO(List<JobConfigDTO> jobConfigDTOList,
      Map<DeployModeEnum, String> map) {
    if (CollectionUtils.isEmpty(jobConfigDTOList)) {
      return Collections.emptyList();
    }

    List<JobConfigVO> list = new ArrayList<JobConfigVO>();

    for (JobConfigDTO jobConfigDTO : jobConfigDTOList) {
      list.add(JobConfigVO.toVO(jobConfigDTO, map));
    }
    return list;
  }

  public static void buildAlarm(List<JobConfigVO> jobConfigVOList,
      Map<Long, List<AlarmTypeEnum>> map) {
    if (CollectionUtils.isEmpty(map)) {
      return;
    }
    for (JobConfigVO jobConfigVO : jobConfigVOList) {
      List<AlarmTypeEnum> list = map.get(jobConfigVO.getId());
      if (CollectionUtil.isNotEmpty(list)) {
        StringBuilder str = new StringBuilder("[");
        for (AlarmTypeEnum alarmTypeEnum : list) {
          switch (alarmTypeEnum) {
            case DINGDING:
              str.append(" 钉钉");
              break;
            case CALLBACK_URL:
              str.append(" 回调");
              break;
            case AUTO_START_JOB:
              str.append(" 自动重启");
              break;
            default:

          }
        }
        str.append("]");
        jobConfigVO.setAlarmStrs(str.toString());
      }
    }

  }
}
