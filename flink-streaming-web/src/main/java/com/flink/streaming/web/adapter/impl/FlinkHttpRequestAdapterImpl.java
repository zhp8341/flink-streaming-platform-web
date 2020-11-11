package com.flink.streaming.web.adapter.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flink.streaming.web.adapter.FlinkHttpRequestAdapter;
import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.model.flink.JobStandaloneInfo;
import com.flink.streaming.web.model.flink.JobYarnInfo;
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
public class FlinkHttpRequestAdapterImpl implements FlinkHttpRequestAdapter {

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public JobYarnInfo getJobInfoForPerYarnByAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            throw new BizException(SysErrorEnum.HTTP_REQUEST_IS_NULL);
        }
        String res = null;
        try {
            String url = systemConfigService.getYarnRmHttpAddress() + FlinkYarnRestUriConstants.getUriJobsForYarn(appId);
            log.info("[getJobInfoForPerYarnByAppId]请求参数 appId={} url={}", appId, url);
            res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
            log.info("[getJobInfoForPerYarnByAppId]请求参数结果: res={}", res);
            if (StringUtils.isEmpty(res)) {
                return null;
            }
            JSONArray jsonArray = (JSONArray) JSON.parseObject(res).get("jobs");
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JobYarnInfo jobYarnInfo = new JobYarnInfo();
            jobYarnInfo.setId((String) jsonObject.get("id"));
            jobYarnInfo.setStatus((String) jsonObject.get("status"));
            return jobYarnInfo;
        } catch (Exception e) {
            log.error("json 异常 res={}", res, e);
        }
        return null;
    }

    @Override
    public JobStandaloneInfo getJobInfoForStandaloneByAppId(String appId, DeployModeEnum deployModeEnum) {
        if (StringUtils.isEmpty(appId)) {
            throw new BizException(SysErrorEnum.HTTP_REQUEST_IS_NULL);
        }
        String res = null;
        JobStandaloneInfo jobStandaloneInfo = null;
        try {
            String url = systemConfigService.getFlinkHttpAddress(deployModeEnum) + FlinkYarnRestUriConstants.getUriJobsForStandalone(appId);
            log.info("[getJobInfoForStandaloneByAppId]请求参数 appId={} url={}", appId, url);
            res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
            log.info("[getJobInfoForStandaloneByAppId]请求参数结果: res={}", res);
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
    public void cancelJobForYarnByAppId(String appId, String jobId) {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(jobId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        String url = systemConfigService.getYarnRmHttpAddress() + FlinkYarnRestUriConstants.getUriCancelForYarn(appId, jobId);
        log.info("[cancelJobByAppId]请求参数 appId={} jobId={} url={}", appId, jobId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
        log.info("[cancelJobByAppId]请求参数结果: res={}", res);
    }

    @Override
    public void cancelJobForFlinkByAppId(String jobId,DeployModeEnum deployModeEnum) {
        if (StringUtils.isEmpty(jobId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        String url = systemConfigService.getFlinkHttpAddress(deployModeEnum) + FlinkYarnRestUriConstants.getUriCancelForStandalone(jobId);
        log.info("[cancelJobForFlinkByAppId]请求参数 jobId={} url={}", jobId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
        log.info("[cancelJobForFlinkByAppId]请求参数结果: res={}", res);
    }

    @Override
    public String getSavepointPath(String appId, String jobId) {

        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(jobId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        String url = systemConfigService.getYarnRmHttpAddress() + FlinkYarnRestUriConstants.getUriCheckpointForYarn(appId, jobId);
        log.info("[getSavepointPath]请求参数 appId={} jobId={} url={}", appId, jobId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_5_M).getForObject(url, String.class);
        log.info("[getSavepointPath]请求参数结果: res={}", res);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        try {
            JSONObject jsonObject = (JSONObject) JSON.parseObject(res).get("latest");
            JSONObject savepoint = (JSONObject) jsonObject.get("savepoint");
            return (String) savepoint.get("external_path");
        } catch (Exception e) {
            log.error("json 异常 res={}", res, e);
        }

        return null;
    }


}
