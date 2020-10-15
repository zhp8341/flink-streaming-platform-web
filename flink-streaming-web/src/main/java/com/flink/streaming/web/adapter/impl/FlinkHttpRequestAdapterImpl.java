package com.flink.streaming.web.adapter.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flink.streaming.web.adapter.FlinkHttpRequestAdapter;
import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.model.flink.JobInfo;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public JobInfo getJobInfoForPerYarnByAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            throw new BizException(SysErrorEnum.HTTP_REQUEST_IS_NULL);
        }

        String url = systemConfigService.getYarnRmHttpAddress() + FlinkYarnRestUriConstants.getUriJobsForYarn(appId);

        log.info("[getJobInfoForPerYarnByAppId]请求参数 appId={} url={}", appId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
        log.info("[getJobInfoForPerYarnByAppId]请求参数结果: res={}", res);
        if (StringUtils.isEmpty(res)) {
            return null;
        }
        try {
            JSONArray jsonArray = (JSONArray) JSON.parseObject(res).get("jobs");
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JobInfo jobInfo = new JobInfo();
            jobInfo.setId((String) jsonObject.get("id"));
            jobInfo.setStatus((String) jsonObject.get("status"));
            return jobInfo;
        } catch (Exception e) {
            log.error("json 异常 res={}", res, e);
        }
        return null;
    }


    @Override
    public void cancelJobByAppId(String appId, String jobId) {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(jobId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        String url = systemConfigService.getYarnRmHttpAddress() + FlinkYarnRestUriConstants.getUriCancelForYarn(appId, jobId);
        log.info("[cancelJobByAppId]请求参数 appId={} jobId={} url={}", appId, jobId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
        log.info("[cancelJobByAppId]请求参数结果: res={}", res);
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
