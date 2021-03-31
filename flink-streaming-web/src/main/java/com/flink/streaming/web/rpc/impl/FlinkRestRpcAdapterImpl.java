package com.flink.streaming.web.rpc.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.flink.streaming.web.rpc.FlinkRestRpcAdapter;
import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.rpc.model.JobInfo;
import com.flink.streaming.web.rpc.model.JobStandaloneInfo;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

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
    public List<String> queryRunJobInfo(DeployModeEnum deployModeEnum) {

        try {
            String url = systemConfigService.getFlinkHttpAddress(deployModeEnum) + FlinkYarnRestUriConstants.URI_JOBS;
            String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
            if (StringUtils.isEmpty(res)
                    || JSON.parseObject(res).get("jobs") == null) {
                return Collections.emptyList();
            }
            List<JobInfo> list = JSON.parseArray(JSON.toJSONString(JSON.parseObject(res).get("jobs")), JobInfo.class);
            if (CollectionUtil.isEmpty(list)) {
                return Collections.emptyList();
            }
            List<String> idList = Lists.newArrayList();
            for (JobInfo jobInfo : list) {
                if (SystemConstants.STATUS_RUNNING.equalsIgnoreCase(jobInfo.getStatus())) {
                    idList.add(jobInfo.getId());
                }
            }
            return idList;
        } catch (Exception e) {
            log.error("[queryRunJobInfo is error]", e);
            throw new BizException(String.format(" queryRunJobInfo is error %s", e.getMessage()));
        }


    }

    @Override
    public JobStandaloneInfo getJobInfoForStandaloneByAppId(String appId, DeployModeEnum deployModeEnum) {
        if (StringUtils.isEmpty(appId)) {
            throw new BizException(SysErrorEnum.HTTP_REQUEST_IS_NULL);
        }
        String res = null;
        JobStandaloneInfo jobStandaloneInfo = null;
        try {
            String url = systemConfigService.getFlinkHttpAddress(deployModeEnum)
                    + FlinkYarnRestUriConstants.getUriJobsForStandalone(appId);

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
    public void cancelJobForFlinkByAppId(String jobId, DeployModeEnum deployModeEnum) {
        if (StringUtils.isEmpty(jobId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        String url = systemConfigService.getFlinkHttpAddress(deployModeEnum)
                + FlinkYarnRestUriConstants.getUriCancelForStandalone(jobId);

        log.info("[cancelJobForFlinkByAppId]请求参数 jobId={} url={}", jobId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
        log.info("[cancelJobForFlinkByAppId]请求参数结果: res={}", res);
    }


}
