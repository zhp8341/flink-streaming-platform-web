package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.ao.AlarmServiceAO;
import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.model.dto.AlartLogDTO;
import com.flink.streaming.web.model.vo.CallbackDTO;
import com.flink.streaming.web.service.AlartLogService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class AlartApiController extends BaseController {


    @Autowired
    private AlarmServiceAO alarmServiceAO;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private AlartLogService alartLogService;


    /**
     * 测试钉钉功能是否正常
     *
     * @author zhuhuipei
     * @date 2020-09-28
     * @time 19:25
     */
    @RequestMapping("/testDingdingAlert")
    public RestResult testDingdingAlert() {
        try {
            String alartUrl = systemConfigService.getSystemConfigByKey(SysConfigEnum.DINGDING_ALARM_URL.getKey());
            if (StringUtils.isEmpty(alartUrl)) {
                return RestResult.error("钉钉告警地址不存在");
            }
            boolean isSuccess = alarmServiceAO.sendForDingding(alartUrl,
                    SystemConstants.buildDingdingMessage("测试"), 0L);
            if (isSuccess) {
                return RestResult.success();
            }
        } catch (Exception e) {
            log.error("testDingdingAlert is fail", e);
        }

        return RestResult.error("钉钉告警测试失败");
    }


    /**
     * 测试url回调告警
     *
     * @author zhuhuipei
     * @date 2021/2/21
     * @time 15:05
     */
    @RequestMapping("/testHttpAlert")
    public RestResult testHttpAlert() {
        try {
            String callbackUrl = systemConfigService.getSystemConfigByKey(SysConfigEnum.CALLBACK_ALARM_URL.getKey());
            if (StringUtils.isEmpty(callbackUrl)) {
                return RestResult.error("回调URL地址不存在");
            }
            CallbackDTO callbackDTO = new CallbackDTO();
            callbackDTO.setAppId("测试AppId");
            callbackDTO.setDeployMode("测试DeployMode");
            callbackDTO.setJobName("测试JobName");
            callbackDTO.setJobConfigId(0L);
            boolean isSuccess = alarmServiceAO.sendForHttp(callbackUrl, callbackDTO);
            if (isSuccess) {
                return RestResult.success();
            }
            return RestResult.error("测试失败");
        } catch (Exception e) {
            log.error("testHttpAlert is fail", e);
        }

        return RestResult.error("钉钉告警测试失败");
    }


    /**
     * 错误日志详情
     *
     * @author zhuhuipei
     * @date 2020-09-28
     * @time 19:25
     */
    @RequestMapping("/logErrorInfo")
    public RestResult logErrorInfo(Long id) {
        AlartLogDTO alartLogDTO = alartLogService.findLogById(id);
        if (alartLogDTO == null || StringUtils.isEmpty(alartLogDTO.getFailLog())) {
            return RestResult.error("没有异常数据");
        }
        return RestResult.success(alartLogDTO.getFailLog());

    }

}
