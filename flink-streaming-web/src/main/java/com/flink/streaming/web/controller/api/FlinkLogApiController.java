package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.common.util.IpUtil;
import com.flink.streaming.web.common.util.LinuxInfoUtil;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/5/5
 * @time 10:23
 */
@RestController
@RequestMapping("/log")
@Slf4j
public class FlinkLogApiController {

    @Autowired
    public SystemConfigService systemConfigService;

    @RequestMapping(value = "/getFlinkLocalJobLog")
    public String  getFlinkLocalJobLog(HttpServletResponse response){
        try {
            String fileName=String.format("flink-%s-client-%s.log", LinuxInfoUtil.loginName(), IpUtil.getHostName());
            String flinkName=systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey());
            String logPath=flinkName+"log/"+fileName;
            File file = new File(logPath);
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            response.addHeader("Content-Length", "" + file.length());
            response.setContentType("text/plain; charset=utf-8");
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream(),2048);
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (Exception ex) {
            log.error("[getFlinkLocalJobLog is error]",ex);
        }
        return "ok";
    }


}
