package com.flink.streaming.web.rpc.impl;

import com.alibaba.fastjson.JSON;
import com.flink.streaming.web.common.FlinkRestUriConstants;
import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.rpc.FlinkRestRpcAdapter;
import com.flink.streaming.web.rpc.model.JobRunInfo;
import com.flink.streaming.web.model.flink.JobRunRequestInfo;
import com.flink.streaming.web.rpc.model.JobStandaloneInfo;
import com.flink.streaming.web.rpc.model.UploadJarInfo;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

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
    public String uploadJarAndReturnJarId(File file, DeployModeEnum deployModeEnum) {
        String url = HttpUtil.buildUrl(systemConfigService.getFlinkHttpAddress(deployModeEnum),
                FlinkRestUriConstants.getUriJobsUploadForStandalone());
        RestTemplate restTemplate = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M);
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        headers.setContentType(type);

        //设置请求体，注意是LinkedMultiValueMap
        FileSystemResource fileSystemResource = new FileSystemResource(file);
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("jarfile", fileSystemResource);
        // 用HttpEntity封装整个请求报文
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        String res = restTemplate.postForObject(url, files, String.class);
        UploadJarInfo info = JSON.parseObject(res, UploadJarInfo.class);
        if (info == null) {
            throw new BizException("上传 jar 失败");
        }
        return info.getJarId();
    }

    @Override
    public String runJarByJarId(String jarId, JobRunRequestInfo jobRunRequestInfo, DeployModeEnum deployModeEnum) {
        String url = HttpUtil.buildUrl(systemConfigService.getFlinkHttpAddress(deployModeEnum),
                FlinkRestUriConstants.getUriJobsRunForStandalone(jarId));
        log.info("runJarByJarId url={}", url);
        HttpEntity<String> request = new HttpEntity<>(JSON.toJSONString(jobRunRequestInfo));
        String res =
                HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).postForEntity(url, request, String.class).getBody();
        JobRunInfo info = JSON.parseObject(res, JobRunInfo.class);
        if (info == null) {
            throw new BizException("run jar 失败");
        }
        return info.getJobId();
    }

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
        String url = HttpUtil.buildUrl(systemConfigService.getFlinkHttpAddress(deployModeEnum),
                FlinkYarnRestUriConstants.getUriCancelForStandalone(jobId));

        log.info("[cancelJobForFlinkByAppId]请求参数 jobId={} url={}", jobId, url);
        String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
        log.info("[cancelJobForFlinkByAppId]请求参数结果: res={}", res);
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
            log.info("[savepointPath]请求参数 jobId={} url={}", jobId, url);
            String res = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M).getForObject(url, String.class);
            if (StringUtils.isEmpty(res)) {
                return null;
            }
            return JSON.parseObject(res).getJSONObject("latest").
                    getJSONObject("savepoint").getString("external_path");

        } catch (Exception e) {
            log.error("savepointPath is error", e);
        }
        return null;


    }


}
