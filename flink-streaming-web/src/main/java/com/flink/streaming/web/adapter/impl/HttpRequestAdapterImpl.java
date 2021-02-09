package com.flink.streaming.web.adapter.impl;

import com.alibaba.fastjson.JSON;
import com.flink.streaming.web.adapter.HttpRequestAdapter;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.HttpUtil;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.YarnStateEnum;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import com.flink.streaming.web.model.to.AppTO;
import com.flink.streaming.web.model.to.YarnAppInfo;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-06
 * @time 20:15
 */
@Service
@Slf4j
public class HttpRequestAdapterImpl implements HttpRequestAdapter {

    private final String BODY_HTTP_KILL = "{\"state\":\"KILLED\"}";

    @Autowired
    private SystemConfigService systemConfigService;

    //TODO 设个方法设计不好 需要改造
    @Override
    public String getAppIdByYarn(String jobName, String queueName) {
        if (StringUtils.isEmpty(jobName)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        String url = systemConfigService.getYarnRmHttpAddress() + SystemConstants.buildHttpQuery(queueName);
        RestTemplate restTemplate = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M);
        log.info("请求参数  url={}", url);
        String res = restTemplate.getForObject(url, String.class);
        log.info("请求结果 str={} url={}", res, url);

        YarnAppInfo yarnAppInfo = JSON.parseObject(res, YarnAppInfo.class);

        this.check( yarnAppInfo, queueName, jobName, url);

        for (AppTO appTO : yarnAppInfo.getApps().getApp()) {
            if (JobConfigDTO.buildRunName(jobName).equals(appTO.getName()) &&
                    SystemConstants.STATUS_RUNNING.equals(appTO.getState())) {
                log.info("任务信息 appTO={}", appTO);
                return appTO.getId();
            }
        }
        throw new BizException("yarn队列" + queueName + "中没有找到运行的任务 name=" +
                JobConfigDTO.buildRunName(jobName), SysErrorEnum.YARN_CODE.getCode());
    }


    @Override
    public void stopJobByJobId(String appId) {
        log.info("执行stopJobByJobId appId={}", appId);
        if (StringUtils.isEmpty(appId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL_YARN_APPID);
        }
        String url = systemConfigService.getYarnRmHttpAddress() + SystemConstants.HTTP_YARN_APPS + appId + "/state";
        log.info("请求关闭 URL ={}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity(BODY_HTTP_KILL, headers);
        RestTemplate restTemplate = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M);
        restTemplate.put(url, httpEntity);
    }

    @Override
    public YarnStateEnum getJobStateByJobId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL_YARN_APPID);
        }

        String url = systemConfigService.getYarnRmHttpAddress() + SystemConstants.HTTP_YARN_APPS + appId + "/state";
        RestTemplate restTemplate = HttpUtil.buildRestTemplate(HttpUtil.TIME_OUT_1_M);
        String res = restTemplate.getForObject(url, String.class);
        if (StringUtils.isEmpty(res)) {
            log.error("请求失败:返回结果为空 url={}", url);
            throw new BizException(SysErrorEnum.HTTP_REQUEST_IS_NULL);
        }
        return YarnStateEnum.getYarnStateEnum(String.valueOf(JSON.parseObject(res).get("state")));
    }


    private void check(YarnAppInfo yarnAppInfo,String queueName,String jobName,String url){
        if (yarnAppInfo == null) {
            log.error("在队列" + queueName + "没有找到任何yarn上的任务 url={}", url);
            throw new BizException("yarn队列" + queueName + "中没有找到运行的任务 name=" +
                    JobConfigDTO.buildRunName(jobName), SysErrorEnum.YARN_CODE.getCode());
        }
        if (yarnAppInfo.getApps() == null) {
            log.error("yarnAppInfo.getApps() is null", yarnAppInfo);
            throw new BizException("yarn队列" + queueName + "中没有找到运行的任务 name=" +
                    JobConfigDTO.buildRunName(jobName), SysErrorEnum.YARN_CODE.getCode());
        }
        if (yarnAppInfo.getApps().getApp()==null||yarnAppInfo.getApps().getApp().size()<=0){
            log.error("yarnAppInfo.getApps().getApp() is null", yarnAppInfo);
            throw new BizException("yarn队列" + queueName + "中没有找到运行的任务 name=" +
                    JobConfigDTO.buildRunName(jobName), SysErrorEnum.YARN_CODE.getCode());
        }
    }

}
