package com.flink.streaming.web.ao.impl;

import com.flink.streaming.web.rpc.FlinkRestRpcAdapter;
import com.flink.streaming.web.rpc.YarnRestRpcAdapter;
import com.flink.streaming.web.rpc.CommandRpcClinetAdapter;
import com.flink.streaming.web.ao.JobBaseServiceAO;
import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.common.MessageConstants;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.enums.JobConfigStatus;
import com.flink.streaming.web.enums.JobTypeEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.dto.JobRunParamDTO;
import com.flink.streaming.web.rpc.model.JobInfo;
import com.flink.streaming.web.service.JobConfigService;
import com.flink.streaming.web.service.JobRunLogService;
import com.flink.streaming.web.service.SavepointBackupService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 23:11
 */
@Component("jobYarnServerAO")
@Slf4j
public class JobYarnServerAOImpl implements JobServerAO {

    //最大重试次数
    private static final Integer tryTimes = 2;

    @Autowired
    private JobConfigService jobConfigService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private YarnRestRpcAdapter yarnRestRpcAdapter;

    @Autowired
    private JobRunLogService jobRunLogService;

    @Autowired
    private CommandRpcClinetAdapter commandRpcClinetAdapter;

    @Autowired
    private FlinkRestRpcAdapter flinkRestRpcAdapter;

    @Autowired
    private SavepointBackupService savepointBackupService;

    @Autowired
    private JobBaseServiceAO jobBaseServiceAO;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void start(Long id, Long savepointId, String userName) {

        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);

        //1、检查jobConfigDTO 状态等参数
        jobBaseServiceAO.checkStart(jobConfigDTO);

        if (StringUtils.isNotEmpty(jobConfigDTO.getJobId())) {
            this.stop(jobConfigDTO);
        }

        //2、将配置的sql 写入本地文件并且返回运行所需参数
        JobRunParamDTO jobRunParamDTO = jobBaseServiceAO.writeSqlToFile(jobConfigDTO);

        //3、插一条运行日志数据
        Long jobRunLogId = jobBaseServiceAO.insertJobRunLog(jobConfigDTO, userName);

        //4、变更任务状态（变更为：启动中） 有乐观锁 防止重复提交
        jobConfigService.updateStatusByStart(jobConfigDTO.getId(), userName, jobRunLogId, jobConfigDTO.getVersion());

        String savepointPath = savepointBackupService.getSavepointPathById(id, savepointId);

        //异步提交任务
        jobBaseServiceAO.aSyncExecJob(jobRunParamDTO, jobConfigDTO, jobRunLogId, savepointPath);

    }


    @Override
    public void stop(Long id, String userName) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }

        //1、停止前做一次savepoint操作
        try {
            this.savepoint(id);
        } catch (Exception e) {
            log.error("autoSavePoint is error");
        }

        //2、停止任务
        this.stop(jobConfigDTO);

        JobConfigDTO jobConfig = new JobConfigDTO();
        jobConfig.setStatus(JobConfigStatus.STOP);
        jobConfig.setEditor(userName);
        jobConfig.setId(id);
        jobConfig.setJobId("");
        //3、变更状态
        jobConfigService.updateJobConfigById(jobConfig);

    }

    @Override
    public void savepoint(Long id) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
        if (StringUtils.isEmpty(jobConfigDTO.getFlinkCheckpointConfig())) {
            log.warn(" FlinkCheckpointConfig is null jobConfigDTO={}", jobConfigDTO);
            return;
        }
        if (StringUtils.isEmpty(jobConfigDTO.getJobId())) {
            log.warn("getJobId is null getJobName={}", jobConfigDTO.getJobName());
            return;
        }
        if (JobTypeEnum.JAR.equals(jobConfigDTO.getJobTypeEnum())) {
            log.warn("自定义jar任务不支持savePoint  任务:{}", jobConfigDTO.getJobName());
            return;
        }

        JobInfo jobInfo = yarnRestRpcAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
        if (jobInfo == null) {
            log.warn("jobInfo is null getJobName={}", jobConfigDTO.getJobName());
            return;
        }
        //1、 执行savepoint
        try {
            commandRpcClinetAdapter.savepointForPerYarn(jobInfo.getId(),
                    SystemConstants.DEFAULT_SAVEPOINT_ROOT_PATH + id, jobConfigDTO.getJobId());
        } catch (Exception e) {
            log.error("savepointForPerYarn is error", e);
            return;
        }

        String savepointPath = yarnRestRpcAdapter.getSavepointPath(jobConfigDTO.getJobId(), jobInfo.getId());
        if (StringUtils.isEmpty(savepointPath)) {
            log.warn("getSavepointPath is null jobConfigDTO={}", jobConfigDTO);
            return;
        }
        //2、 执行保存Savepoint到本地数据库
        savepointBackupService.insertSavepoint(id, savepointPath, new Date());
    }


    @Override
    public void open(Long id, String userName) {
        jobConfigService.openOrClose(id, YN.Y, userName);
    }

    @Override
    public void close(Long id, String userName) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO.getStatus().equals(JobConfigStatus.RUN)) {
            throw new BizException(MessageConstants.MESSAGE_002);
        }
        if (jobConfigDTO.getStatus().equals(JobConfigStatus.STARTING)) {
            throw new BizException(MessageConstants.MESSAGE_003);
        }
        jobConfigService.openOrClose(id, YN.N, userName);
    }


    private void stop(JobConfigDTO jobConfigDTO) {
        Integer retryNum = 1;
        while (retryNum <= tryTimes) {
            JobInfo jobInfo = yarnRestRpcAdapter.getJobInfoForPerYarnByAppId(jobConfigDTO.getJobId());
            if (jobInfo != null && SystemConstants.STATUS_RUNNING.equals(jobInfo.getStatus())) {
                log.info("执行停止操作 jobYarnInfo={} retryNum={} id={}", jobInfo, retryNum, jobConfigDTO.getJobId());
                yarnRestRpcAdapter.cancelJobForYarnByAppId(jobConfigDTO.getJobId(), jobInfo.getId());
            } else {
                log.info("任务已经停止 jobYarnInfo={} id={}", jobInfo, jobConfigDTO.getJobId());
                break;
            }
            retryNum++;
        }
    }
}
