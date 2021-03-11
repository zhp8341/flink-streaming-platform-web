package com.flink.streaming.web.adapter.impl;

import com.flink.streaming.web.adapter.CommandAdapter;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.TipsConstants;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.service.JobRunLogService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 20:13
 */
@Slf4j
@Service
public class CommandAdapterImpl implements CommandAdapter {

    private static long INTERVAL_TIME = 1000 * 5;

    private static long INTERVAL_TIME_ONE = 1000 * 2;

    @Autowired
    private JobRunLogService jobRunLogService;


    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void startForPerYarn(String command, StringBuilder localLog, Long jobRunLogId) throws Exception {

        long lastTime = System.currentTimeMillis();
        byte[] buffer = new byte[1024];
        log.info(" command ={} ", command);
        localLog.append("启动命令：").append(command).append("\n");
        Process pcs = Runtime.getRuntime().exec(command);
        BufferedInputStream reader = new BufferedInputStream(pcs.getInputStream());
        int bytesRead = 0;
        while ((bytesRead = reader.read(buffer)) != -1) {
            String result = new String(buffer, 0, bytesRead, "UTF-8");
            log.info(result);
            localLog.append(result).append("\n");

            //每隔5s更新日志
            if (System.currentTimeMillis() >= lastTime + INTERVAL_TIME) {
                jobRunLogService.updateLogById(localLog.toString(), jobRunLogId);
                lastTime = System.currentTimeMillis();
            }
        }
        int rs = pcs.waitFor();
        localLog.append("rs=").append(rs).append("\n");
        if (rs != 0) {
            localLog.append("pcs.waitFor() 执行异常 rs=").append(rs);
            throw new Exception("pcs.waitFor() is error  rs=" + rs);
        }
    }

    //TODO
    @Override
    public String startForLocal(String command, StringBuilder localLog, Long jobRunLogId) throws Exception {
        long lastTime = System.currentTimeMillis();
        byte[] buffer = new byte[1024];
        log.info(" command ={} ", command);
        localLog.append("启动命令：").append(command).append("\n");
        Process pcs = Runtime.getRuntime().exec(command);
        BufferedInputStream reader = new BufferedInputStream(pcs.getInputStream());
        int bytesRead = 0;

        String appId = null;
        while ((bytesRead = reader.read(buffer)) != -1) {
            String result = new String(buffer, 0, bytesRead, "UTF-8");
            log.info(result);
            if (result.contains("Job has been submitted with JobID")) {
                appId = result.replace("Job has been submitted with JobID", "").trim();
            }
            localLog.append(result).append("\n");

            //每隔1s更新日志
            if (System.currentTimeMillis() >= lastTime + INTERVAL_TIME_ONE) {
                jobRunLogService.updateLogById(localLog.toString(), jobRunLogId);
                lastTime = System.currentTimeMillis();
            }
        }
        int rs = pcs.waitFor();
        localLog.append("rs=").append(rs).append("\n");
        jobRunLogService.updateLogById(localLog.toString(), jobRunLogId);
        if (rs != 0) {
            localLog.append("pcs.waitFor() 执行异常 rs=").append(rs).append("   appId=").append(appId);
            throw new RuntimeException("pcs.waitFor() is error  rs=" + rs);
        }
        if (StringUtils.isEmpty(appId)) {
            localLog.append("appId无法获 ").append(TipsConstants.TIPS_1);
            throw new RuntimeException("appId无法获取");
        }
        return appId;
    }

    @Override
    public void savepointForPerYarn(String jobId, String targetDirectory, String yarnAppId) throws Exception {
        String command = this.buildSavepointCommand(jobId, targetDirectory, yarnAppId);
        log.info("[savepointForPerYarn] command={}", command);
        Process pcs = Runtime.getRuntime().exec(command);
        BufferedInputStream reader = new BufferedInputStream(pcs.getInputStream());
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = reader.read(buffer)) != -1) {
            String result = new String(buffer, 0, bytesRead, "UTF-8");
            log.info(result);
        }
        int rs = pcs.waitFor();
        if (rs != 0) {
            throw new Exception("pcs.waitFor() is error  rs=" + rs);
        }
    }


    private String buildSavepointCommand(String jobId, String targetDirectory, String yarnAppId) {
        StringBuilder command = new StringBuilder(SystemConstants.buildFlinkBin(systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey())));
        command.append(" savepoint ")
                .append(jobId).append(" ")
                .append(targetDirectory).append(" ")
                .append("-yid ").append(yarnAppId);
        return command.toString();
    }
}
