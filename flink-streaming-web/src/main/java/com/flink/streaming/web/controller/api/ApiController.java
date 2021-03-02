package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.controller.web.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@RestController
@Slf4j
public class ApiController extends BaseController {

    @RequestMapping("/ok")
    public RestResult ok() {
        return RestResult.success();
    }

    @RequestMapping("/alarmCallback")
    public RestResult alarmCallback(String appId, String jobName, String deployMode) {
        log.info("测试回调 appId={} jobName={} deployMode={}", appId, jobName, deployMode);
        return RestResult.success();
    }

}
