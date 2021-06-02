package com.flink.streaming.web.controller.api;

import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.JobTypeEnum;
import com.flink.streaming.common.model.CheckPointParam;
import com.flink.streaming.web.ao.JobConfigAO;
import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.common.FlinkConstants;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.enums.*;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.common.util.CliConfigUtil;
import com.flink.streaming.web.common.util.MatcherUtils;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.param.UpsertJobConfigParam;
import com.flink.streaming.web.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class JobConfigApiController extends BaseController {


    @Autowired
    private JobServerAO jobYarnServerAO;

    @Autowired
    private JobServerAO jobStandaloneServerAO;

    @Autowired
    private JobConfigService jobConfigService;
    @Autowired
    private JobConfigAO jobConfigAO;

    @RequestMapping("/start")
    public RestResult<String> start(Long id, Long savepointId) {
        try {
            this.getJobServerAO(id).start(id, savepointId, this.getUserName());
        } catch (BizException e) {
            log.error("启动失败 id={}", id, e);
            return RestResult.error(e.getCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("启动失败 id={}", id, e);
            return RestResult.error(SysErrorEnum.START_JOB_FAIL);
        }
        return RestResult.success();
    }

    @RequestMapping("/stop")
    public RestResult<String> stop(Long id) {
        try {
            this.getJobServerAO(id).stop(id, this.getUserName());
        } catch (BizException e) {
            log.warn("停止失败 id={}", id, e);
            return RestResult.error(e.getCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("停止失败 id={}", id, e);
            return RestResult.error(SysErrorEnum.STOP_JOB_FAIL);
        }
        return RestResult.success();
    }


    @RequestMapping("/close")
    public RestResult<String> close(Long id) {
        try {
            this.getJobServerAO(id).close(id, this.getUserName());
        } catch (BizException e) {
            log.warn("关闭失败 id={}", id, e);
            return RestResult.error(e.getCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("关闭失败 id={}", id, e);
            return RestResult.error(SysErrorEnum.START_JOB_FAIL);
        }
        return RestResult.success();
    }


    @RequestMapping("/open")
    public RestResult<String> open(Long id) {
        try {
            this.getJobServerAO(id).open(id, this.getUserName());
        } catch (BizException e) {
            log.warn("开始失败 id={}", id, e);
            return RestResult.error(e.getCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("开始失败 id={}", id, e);
            return RestResult.error(SysErrorEnum.START_JOB_FAIL);
        }
        return RestResult.success();
    }

    @RequestMapping("/delete")
    public RestResult<String> delete(Long id) {
        try {
            jobConfigService.deleteJobConfigById(id, this.getUserName());
        } catch (BizException e) {
            log.warn("删除失败 id={}", id, e);
            return RestResult.error(e.getCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("删除失败 id={}", id, e);
            return RestResult.error(SysErrorEnum.START_JOB_FAIL);
        }
        return RestResult.success();
    }


    @RequestMapping("/savepoint")
    public RestResult<String> savepoint(Long id) {
        try {
            this.getJobServerAO(id).savepoint(id);
        } catch (BizException e) {
            log.warn("savepoint is error id={}", id, e);
            return RestResult.error(e.getCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("savepoint is error id={}", id, e);
            return RestResult.error(SysErrorEnum.SAVEPOINT_JOB_FAIL);
        }
        return RestResult.success();
    }


    @RequestMapping(value = "/addConfig", method = {RequestMethod.POST})
    public RestResult addConfig(UpsertJobConfigParam upsertJobConfigParam) {

        try {
            RestResult restResult = checkUpsertJobConfigParam(upsertJobConfigParam);
            if (restResult != null) {
                return restResult;
            }
            jobConfigAO.addJobConfig(UpsertJobConfigParam.toDTO(upsertJobConfigParam));
        } catch (BizException biz) {
            log.warn("addJobConfig is error ", biz);
            return RestResult.error(biz.getErrorMsg());
        } catch (Exception e) {
            log.error("addJobConfig is error", e);
            return RestResult.error(e.getMessage());
        }
        return RestResult.success();
    }


    @RequestMapping(value = "/editConfig", method = {RequestMethod.POST})
    public RestResult editConfig(UpsertJobConfigParam upsertJobConfigParam) {

        try {
            RestResult restResult = checkUpsertJobConfigParam(upsertJobConfigParam);
            if (restResult != null) {
                return restResult;
            }
            JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(upsertJobConfigParam.getId());
            if (jobConfigDTO == null) {
                return RestResult.error("数据不存在");
            }
            if (YN.getYNByValue(jobConfigDTO.getIsOpen()).getCode()) {
                return RestResult.error(SysErrorEnum.JOB_CONFIG_JOB_IS_OPEN.getErrorMsg());
            }
            jobConfigAO.updateJobConfigById(UpsertJobConfigParam.toDTO(upsertJobConfigParam));
        } catch (BizException biz) {
            log.warn("updateJobConfigById is error ", biz);
            return RestResult.error(biz.getErrorMsg());
        } catch (Exception e) {
            log.error("updateJobConfigById is error", e);
            return RestResult.error(e.getMessage());
        }
        return RestResult.success();
    }

    @RequestMapping(value = "/copyConfig", method = {RequestMethod.POST})
    public RestResult copyConfig(UpsertJobConfigParam upsertJobConfigParam) {
        try {
            JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(upsertJobConfigParam.getId());
            if (jobConfigDTO == null) {
                return RestResult.error("原始拷贝数据不存在");
            }
            /*
             copy job conf
             默认将id去除
             默认在任务名称后面copy_随机字符_${jobConfigDTO.getJobName()}字符
             状态默认重置为停止
             开启配置 isOpen 0
             */
            jobConfigDTO.setId(null);
            jobConfigDTO.setJobName(String.format("copy_%s_%s",
                    StringUtils.lowerCase(RandomStringUtils.randomAlphanumeric(4)),jobConfigDTO.getJobName()));
            jobConfigDTO.setStatus(JobConfigStatus.STOP);
            jobConfigDTO.setIsOpen(YN.N.getValue());
            jobConfigDTO.setJobId(null);
            jobConfigDTO.setLastRunLogId(null);
            jobConfigDTO.setVersion(0);
            jobConfigDTO.setLastStartTime(null);
            jobConfigDTO.setLastRunLogId(null);

            jobConfigAO.addJobConfig(jobConfigDTO);

        } catch (BizException biz) {
            log.warn("copyJobConfigById is error ", biz);
            return RestResult.error(biz.getErrorMsg());
        } catch (Exception e) {
            log.error("copyJobConfigById is error", e);
            return RestResult.error(e.getMessage());
        }
        return RestResult.success();
    }

    private RestResult checkUpsertJobConfigParam(UpsertJobConfigParam upsertJobConfigParam) {
        if (upsertJobConfigParam == null) {
            return RestResult.error("参数不能空");
        }
        if (StringUtils.isEmpty(upsertJobConfigParam.getJobName())) {
            return RestResult.error("任务名称不能空");
        }
        if (upsertJobConfigParam.getJobName().length() > 50) {
            return RestResult.error("任务名称不能超过50个字符");
        }
        if (!upsertJobConfigParam.getJobName().matches("[0-9A-Za-z_]*")) {
            return RestResult.error("任务名称仅能含数字,字母和下划线");
        }


        //jar需要校验参数
        if (JobTypeEnum.JAR.equals(upsertJobConfigParam.getJobType())){

            if (StringUtils.isEmpty(upsertJobConfigParam.getCustomMainClass())){
                return RestResult.error("主类不能为空");
            }

            if (StringUtils.isEmpty(upsertJobConfigParam.getCustomJarUrl())){
                return RestResult.error("主类jar的http地址不能为空");
            }
            if (MatcherUtils.isHttpsOrHttp(upsertJobConfigParam.getCustomJarUrl())){
                return RestResult.error("主类jar的http地址 不是http或者https:" + upsertJobConfigParam.getCustomJarUrl());
            }
        }
        //sql配置需要校验的参数JobType=null是兼容之前配置
        if (JobTypeEnum.SQL_STREAMING.equals(upsertJobConfigParam.getJobType())
                || upsertJobConfigParam.getJobType()==null
                || JobTypeEnum.SQL_STREAMING.getCode()==upsertJobConfigParam.getJobType().intValue()){
            if (StringUtils.isEmpty(upsertJobConfigParam.getFlinkSql())) {
                return RestResult.error("sql语句不能为空");
            }
            if (StringUtils.isNotEmpty(upsertJobConfigParam.getExtJarPath())) {
                String[] urls = upsertJobConfigParam.getExtJarPath().split(SystemConstant.LINE_FEED);
                for (String url : urls) {
                    if (StringUtils.isEmpty(url)) {
                        continue;
                    }
                    if (!MatcherUtils.isHttpsOrHttp(url)) {
                        return RestResult.error("udf地址错误： 非法的http或者是https地址 url=" + url);
                    }
                }
            }
        }



        if (StringUtils.isNotEmpty(upsertJobConfigParam.getFlinkCheckpointConfig())) {
            CheckPointParam checkPointParam = CliConfigUtil
                    .checkFlinkCheckPoint(upsertJobConfigParam.getFlinkCheckpointConfig());
            RestResult restResult = this.checkPointParam(checkPointParam);
            if (restResult != null && !restResult.isSuccess()) {
                return restResult;
            }
        }


        if (DeployModeEnum.YARN_PER.name().equals(upsertJobConfigParam.getDeployMode())) {
            if (StringUtils.isEmpty(upsertJobConfigParam.getFlinkRunConfig())) {
                return RestResult.error("flink运行配置不能为空");
            }
            RestResult restResult = CliConfigUtil.checkFlinkRunConfigForYarn(upsertJobConfigParam.getFlinkRunConfig());
            if (restResult != null) {
                return restResult;
            }
        }

        return null;
    }


    /**
     * 获取JobServerAO
     *
     * @author zhuhuipei
     * @date 2020/11/4
     * @time 11:19
     */
    private JobServerAO getJobServerAO(Long id) {
        JobConfigDTO jobConfigDTO = jobConfigService.getJobConfigById(id);
        if (jobConfigDTO == null) {
            throw new BizException(SysErrorEnum.JOB_CONFIG_JOB_IS_NOT_EXIST);
        }
        DeployModeEnum deployModeEnum = jobConfigDTO.getDeployModeEnum();
        switch (deployModeEnum) {
            case LOCAL:
                log.info(" 本地模式启动 {}", deployModeEnum);
                return jobStandaloneServerAO;
            case YARN_PER:
                log.info(" yan per 模式启动 {}", deployModeEnum);
                return jobYarnServerAO;
            case STANDALONE:
                log.info(" STANDALONE模式启动 {}", deployModeEnum);
                return jobStandaloneServerAO;
            default:
                throw new RuntimeException("不支持该模式系统");
        }
    }


    private RestResult checkPointParam(CheckPointParam checkPointParam) {
        if (checkPointParam == null) {
            return RestResult.success();
        }
        if (StringUtils.isNotEmpty(checkPointParam.getCheckpointingMode())) {
            if (!(FlinkConstants.EXACTLY_ONCE.equalsIgnoreCase(checkPointParam.getCheckpointingMode())
                    || FlinkConstants.AT_LEAST_ONCE.equalsIgnoreCase(checkPointParam.getCheckpointingMode()))) {
                return RestResult.error("checkpointingMode 参数必须是  AT_LEAST_ONCE 或者 EXACTLY_ONCE");
            }
        }
        if (StringUtils.isNotEmpty(checkPointParam.getExternalizedCheckpointCleanup())) {
            if (!(FlinkConstants.DELETE_ON_CANCELLATION.equalsIgnoreCase(checkPointParam.getExternalizedCheckpointCleanup())
                    || FlinkConstants.RETAIN_ON_CANCELLATION.equalsIgnoreCase(checkPointParam.getExternalizedCheckpointCleanup()))) {
                return RestResult.error("externalizedCheckpointCleanup 参数必须是DELETE_ON_CANCELLATION 或者 RETAIN_ON_CANCELLATION");
            }
        }
        return RestResult.success();
    }

}
