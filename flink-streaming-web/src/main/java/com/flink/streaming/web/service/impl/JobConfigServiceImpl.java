package com.flink.streaming.web.service.impl;

import com.flink.streaming.common.enums.JobTypeEnum;
import com.flink.streaming.web.enums.*;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.mapper.JobConfigMapper;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobConfigHistoryDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.entity.JobConfig;
import com.flink.streaming.web.model.param.JobConfigParam;
import com.flink.streaming.web.service.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-14
 * @time 19:02
 */
@Slf4j
@Service
public class JobConfigServiceImpl implements JobConfigService {


    @Autowired
    private JobConfigMapper jobConfigMapper;

    @Autowired
    private JobRunLogService jobRunLogService;

    @Autowired
    private JobAlarmConfigService jobAlarmConfigService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private JobConfigHistoryService jobConfigHistoryService;

    @Override
    public Long addJobConfig(JobConfigDTO jobConfigDTO) {
        if (jobConfigDTO == null) {
            return null;
        }
        this.checkJobName(jobConfigDTO.getJobName(), jobConfigDTO.getId());
        this.checkSystemConfig(jobConfigDTO.getDeployModeEnum());
        JobConfig jobConfig = JobConfigDTO.toEntity(jobConfigDTO);
        jobConfigMapper.insert(jobConfig);
        this.insertJobConfigHistory(jobConfig.getId());
        return jobConfig.getId();
    }

    @Override
    public void updateJobConfigById(JobConfigDTO jobConfigDTO) {

        if (jobConfigDTO == null || jobConfigDTO.getId() == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_PARAM_IS_NULL);
        }
        JobConfig jobConfig = jobConfigMapper.selectByPrimaryKey(jobConfigDTO.getId());
        if (jobConfig == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
        if (jobConfigDTO.getDeployModeEnum() == null) {
            this.checkSystemConfig(DeployModeEnum.valueOf(jobConfig.getDeployMode()));
        } else {
            this.checkSystemConfig(jobConfigDTO.getDeployModeEnum());
        }

        if (StringUtils.isNotEmpty(jobConfigDTO.getJobName())) {
            this.checkJobName(jobConfigDTO.getJobName(), jobConfigDTO.getId());
        }
        JobConfig JobConfigUpdate = JobConfigDTO.toEntity(jobConfigDTO);

        jobConfigMapper.updateByPrimaryKeySelective(JobConfigUpdate);

        this.insertJobConfigHistory(jobConfigDTO.getId());
    }

    @Override
    public void updateJobConfigStatusById(Long id, JobConfigStatus jobConfigStatus) {
        JobConfig jobConfig = new JobConfig();
        jobConfig.setId(id);
        jobConfig.setStauts(jobConfigStatus.getCode());
        jobConfigMapper.updateByPrimaryKeySelective(jobConfig);

    }

    @Override
    public void updateStatusByStart(Long id, String userName,
                                    Long jobRunLogId, Integer version) {
        int num = jobConfigMapper.updateStatusByStart(id, JobConfigStatus.STARTING.getCode(), userName,
                jobRunLogId, version);
        if (num < 1) {
            throw new BizException("启动状态更新失败");
        }
    }


    @Override
    public JobConfigDTO getJobConfigById(Long id) {
        if (id == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_PARAM_IS_NULL);
        }

        JobConfigDTO jobConfigDTO = JobConfigDTO.toDTO(jobConfigMapper.selectByPrimaryKey(id));

        if (jobConfigDTO != null) {
            jobConfigDTO.setAlarmTypeEnumList(jobAlarmConfigService.findByJobId(id));
        }

        return jobConfigDTO;
    }

    @Override
    public void openOrClose(Long id, YN yn, String userName) {
        if (id == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_PARAM_IS_NULL);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setId(id);
        jobConfig.setIsOpen(yn.getValue());
        jobConfig.setEditor(userName);
        jobConfigMapper.updateByPrimaryKeySelective(jobConfig);

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobConfigById(Long id, String userName) {
        JobConfigDTO jobConfigDTO = this.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException("配置不存在！");
        }
        if (JobConfigStatus.RUN.equals(jobConfigDTO.getStatus()) ||
                JobConfigStatus.STARTING.equals(jobConfigDTO.getStatus())) {
            throw new BizException("任务正在启动中或者正在运行，请先停止任务");
        }
        if (YN.Y.getValue() == jobConfigDTO.getIsOpen().intValue()) {
            throw new BizException("请先关闭配置");
        }
        jobConfigMapper.deleteById(id, userName);
        jobRunLogService.deleteLogByJobConfigId(id);
    }

    @Override
    public PageModel<JobConfigDTO> queryJobConfig(JobConfigParam jobConfigParam) {
        if (jobConfigParam == null) {
            jobConfigParam = new JobConfigParam();
        }

        PageHelper.startPage(jobConfigParam.getPageNum(), jobConfigParam.getPageSize(), YN.Y.getCode());

        Page<JobConfig> page = jobConfigMapper.findJobConfig(jobConfigParam);
        if (page == null) {
            return null;
        }
        PageModel<JobConfigDTO> pageModel = new PageModel<JobConfigDTO>();
        pageModel.setPageNum(page.getPageNum());
        pageModel.setPages(page.getPages());
        pageModel.setPageSize(page.getPageSize());
        pageModel.setTotal(page.getTotal());
        pageModel.addAll(JobConfigDTO.toListDTO(page.getResult()));
        return pageModel;
    }

    @Override
    public List<JobConfigDTO> findJobConfigByStatus(Integer... status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<Integer> statusList = new ArrayList<>();
        for (Integer s : status) {
            statusList.add(s);
        }
        return JobConfigDTO.toListDTO(jobConfigMapper.findJobConfigByStatus(statusList));
    }


    /**
     * 检查任务名称是不是重复
     *
     * @author zhuhuipei
     * @date 2020-07-14
     * @time 13:56
     */
    private void checkJobName(String jobName, Long id) {
        long count = jobConfigMapper.selectCountByJobName(jobName, id);
        if (count >= 1) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_NAME_IS_EXIST);
        }
    }

    /**
     * 检查配置文件
     *
     * @author zhuhuipei
     * @date 2020/11/4
     * @time 22:43
     */
    private void checkSystemConfig(DeployModeEnum deployModeEnum) {
        StringBuffer tips = new StringBuffer();
        String flinkHome = systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey());
        if (StringUtils.isEmpty(flinkHome)) {
            tips.append(" flinkHome、");
        }
        String webHome = systemConfigService.getSystemConfigByKey(
                SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey());
        if (StringUtils.isEmpty(webHome)) {
            tips.append(" web应用安装的目录 ");
        }
        switch (deployModeEnum) {
            case YARN_PER:
                String yarnHttpAddress = systemConfigService.getSystemConfigByKey(
                        SysConfigEnum.YARN_RM_HTTP_ADDRESS.getKey());
                if (StringUtils.isEmpty(yarnHttpAddress)) {
                    tips.append(" yarn_rm_http_address url地址 ");
                }
                break;
            case LOCAL:
                String flinkHttpAddress = systemConfigService.getSystemConfigByKey(
                        SysConfigEnum.FLINK_REST_HTTP_ADDRESS.getKey());
                if (StringUtils.isEmpty(flinkHttpAddress)) {
                    tips.append(" flink_rest_http_address url地址 ");
                }
                break;
            case STANDALONE:
                String flinkHaHttpAddress =
                        systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_REST_HA_HTTP_ADDRESS.getKey());
                if (StringUtils.isEmpty(flinkHaHttpAddress)) {
                    tips.append(" flink_rest_ha_http_address url地址 ");
                }
                break;
            default:
                break;
        }
        if (StringUtils.isNotEmpty(tips.toString())) {
            throw new BizException("请在  系统管理-->系统设置 里面配置 " + tips.toString());
        }

    }

    private void insertJobConfigHistory(Long id) {
        JobConfig jobConfig = jobConfigMapper.selectByPrimaryKey(id);
        if (jobConfig == null) {
            log.warn("[insertJobConfigHistory] jobConfig is null id:{} ", id);
            return;
        }
        if (JobTypeEnum.SQL_STREAMING.getCode() == jobConfig.getJobType().intValue() ||
                JobTypeEnum.SQL_BATCH.getCode() == jobConfig.getJobType().intValue()) {
            jobConfigHistoryService.insertJobConfigHistory(JobConfigHistoryDTO.to(jobConfig));
        }


    }
}
