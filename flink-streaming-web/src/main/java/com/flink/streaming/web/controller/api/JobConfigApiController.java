package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.CliConfigUtil;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.param.CheckPointParam;
import com.flink.streaming.web.model.param.UpsertJobConfigParam;
import com.flink.streaming.web.service.JobConfigService;
import lombok.extern.slf4j.Slf4j;
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
            return RestResult.error(SysErrorEnum.START_JOB_FAIL);
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
            jobConfigService.addJobConfig(UpsertJobConfigParam.toDTO(upsertJobConfigParam));
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
            jobConfigService.updateJobConfigById(UpsertJobConfigParam.toDTO(upsertJobConfigParam));
        } catch (BizException biz) {
            log.warn("updateJobConfigById is error ", biz);
            return RestResult.error(biz.getErrorMsg());
        } catch (Exception e) {
            log.error("updateJobConfigById is error", e);
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
        if (StringUtils.isEmpty(upsertJobConfigParam.getFlinkSql())) {
            return RestResult.error("sql语句不能为空");
        }
        if (StringUtils.isNotEmpty(upsertJobConfigParam.getFlinkCheckpointConfig())) {

            CheckPointParam checkPointParam = CliConfigUtil.
                    checkFlinkCheckPoint(upsertJobConfigParam.getFlinkCheckpointConfig());
            if (checkPointParam != null && StringUtils.isNotEmpty(checkPointParam.getCheckpointingMode())) {
                if (!("EXACTLY_ONCE".equals(checkPointParam.getCheckpointingMode().toUpperCase())
                        || "AT_LEAST_ONCE".equals(checkPointParam.getCheckpointingMode().toUpperCase()))) {
                    return RestResult.error("checkpointingMode 参数必须是  AT_LEAST_ONCE 或者 EXACTLY_ONCE");
                }
            }
            if (checkPointParam != null && StringUtils.isNotEmpty(checkPointParam.getExternalizedCheckpointCleanup())) {
                if (!("DELETE_ON_CANCELLATION".equals(checkPointParam.getExternalizedCheckpointCleanup().toUpperCase())
                    || "RETAIN_ON_CANCELLATION".equals(checkPointParam.getExternalizedCheckpointCleanup().toUpperCase()))) {
                    return RestResult.error("externalizedCheckpointCleanup 参数必须是 DELETE_ON_CANCELLATION  或者 RETAIN_ON_CANCELLATION");
                }
            }
        }

        if (StringUtils.isNotEmpty(upsertJobConfigParam.getExtJarPath())) {
            String[] urls = upsertJobConfigParam.getExtJarPath().split("\n");
            for (String url : urls) {
                if (StringUtils.isEmpty(url)) {
                    continue;
                }
                if (!HttpUtil.isHttpsOrHttp(url)) {
                    return RestResult.error("udf地址错误： 非法的http或者是https地址 url=" + url);
                }
            }
        }

        if (DeployModeEnum.YARN_PER.name().equals(upsertJobConfigParam.getDeployMode())) {
            if (StringUtils.isEmpty(upsertJobConfigParam.getFlinkRunConfig())) {
                return RestResult.error("flink运行配置不能为空");
            }
            RestResult restResult = CliConfigUtil.checkFlinkRunConfig(upsertJobConfigParam.getFlinkRunConfig());
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
                throw new RuntimeException("不支持改模式系统");
        }
    }

}
