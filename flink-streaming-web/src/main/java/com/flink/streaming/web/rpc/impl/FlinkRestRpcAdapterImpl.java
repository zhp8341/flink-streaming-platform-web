package com.flink.streaming.web.rpc.impl;

import com.alibaba.fastjson.JSON;
import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.rpc.FlinkRestRpcAdapter;
import com.flink.streaming.web.rpc.model.JobStandaloneInfo;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 23:43
 */
@Service
@Slf4j
public class FlinkRestRpcAdapterImpl implements FlinkRestRpcAdapter {

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public JobStandaloneInfo getJobInfoForStandaloneByAppId(String appId, DeployModeEnum deployModeEnum) {
        if (StringUtils.isEmpty(appId)) {
            throw new BizException(SysErrorEnum.HTTP_REQUEST_IS_NULL);
        }
        String res = null;
        JobStandaloneInfo jobStandaloneInfo = null;
        try {
            String url = HttpUtil.buildUrl(systemConfigService.getFlinkHttpAddress(deployModeEnum),
                    FlinkYarnRestUriConstants.getUriJobsForStandalone(appId));
            log.info("请求参数：jobId={}, url={}", appId, url);
            res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
            log.info("请求参数：jobId={}, url={}, result={}", appId, url, res);
            if (StringUtils.isEmpty(res)) {
                return null;
            }
            jobStandaloneInfo = JSON.parseObject(res, JobStandaloneInfo.class);
            return jobStandaloneInfo;
        } catch (HttpClientErrorException e) {
            jobStandaloneInfo = new JobStandaloneInfo();
            jobStandaloneInfo.setErrors(e.getMessage());
            log.error("json 异常 res={}", res, e);
        } catch (Exception e) {
            log.error("json 异常 res={}", res, e);
        }
        return jobStandaloneInfo;
    }

    @Override
    public void cancelJobForFlinkByAppId(String jobId, DeployModeEnum deployModeEnum) {
        if (StringUtils.isEmpty(jobId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        String url = HttpUtil.buildUrl(systemConfigService.getFlinkHttpAddress(deployModeEnum),
                FlinkYarnRestUriConstants.getUriCancelForStandalone(jobId));
        log.info("请求参数：jobId={}, url={}", jobId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
        log.info("请求参数：jobId={}, url={}, result={}", jobId, url, res);
    }

    @Override
    public String savepointPath(String jobId, DeployModeEnum deployModeEnum) {
        if (StringUtils.isEmpty(jobId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        try {
            Thread.sleep(HttpUtil.TIME_OUT_3_S);
            String url = HttpUtil.buildUrl(systemConfigService.getFlinkHttpAddress(deployModeEnum),
                    FlinkYarnRestUriConstants.getUriCheckpoints(jobId));
            log.info("请求参数：jobId={}, url={}", jobId, url);
            String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
            if (StringUtils.isEmpty(res)) {
                return null;
            }
            return JSON.parseObject(res).getJSONObject("latest").getJSONObject("savepoint").getString("external_path");
        } catch (Exception e) {
            log.error("savepointPath is error", e);
        }
        return null;
    }
}
