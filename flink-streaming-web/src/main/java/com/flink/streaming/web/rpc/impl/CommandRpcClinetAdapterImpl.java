package com.flink.streaming.web.rpc.impl;

import cn.hutool.core.date.DateUtil;
import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.TipsConstants;
import com.flink.streaming.web.common.util.CommandUtil;
import com.flink.streaming.web.config.WaitForPoolConfig;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.rpc.CommandRpcClinetAdapter;
import com.flink.streaming.web.service.JobRunLogService;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 20:13
 */
@Slf4j
@Service
public class CommandRpcClinetAdapterImpl implements CommandRpcClinetAdapter {


    private static long INTERVAL_TIME_TWO = 1000 * 2;

    @Autowired
    private JobRunLogService jobRunLogService;


    @Autowired
    private SystemConfigService systemConfigService;


    @Override
    public String submitJob(String command, StringBuilder localLog, Long jobRunLogId,
                            DeployModeEnum deployModeEnum) throws Exception {
        log.info(" command ={} ", command);
        localLog.append("启动命令：").append(command).append(SystemConstant.LINE_FEED);
        Process pcs = Runtime.getRuntime().exec(command);

        //清理错误日志
        this.clearLogStream(pcs.getErrorStream(), String.format("%s#startForLocal-error#%s", DateUtil.now(),
                deployModeEnum.name()));
        String appId = this.clearInfoLogStream(pcs.getInputStream(), localLog, jobRunLogId);
        int rs = pcs.waitFor();
        localLog.append("rs=").append(rs).append(SystemConstant.LINE_FEED);
        jobRunLogService.updateLogById(localLog.toString(), jobRunLogId);
        if (rs != 0) {
            localLog.append(" 执行异常 rs=").append(rs).append("   appId=").append(appId);
            throw new RuntimeException("执行异常 is error  rs=" + rs);
        }
        if (StringUtils.isEmpty(appId)) {
            localLog.append("appId无法获 ").append(TipsConstants.TIPS_1);
            throw new RuntimeException("appId无法获取");
        }
        return appId;
    }


    @Override
    public void savepointForPerYarn(String jobId, String targetDirectory, String yarnAppId) throws Exception {

        String command = CommandUtil.buildSavepointCommandForYarn(jobId, targetDirectory, yarnAppId,
                systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey()));
        log.info("[savepointForPerYarn] command={}", command);
        this.execSavepoint(command);

    }

    @Override
    public void savepointForPerCluster(String jobId, String targetDirectory) throws Exception {
        String command = CommandUtil.buildSavepointCommandForCluster(jobId, targetDirectory,
                systemConfigService.getSystemConfigByKey(SysConfigEnum.FLINK_HOME.getKey()));
        log.info("[savepointForPerCluster] command={}", command);
        this.execSavepoint(command);
    }


    private void execSavepoint(String command) throws Exception {
        Process pcs = Runtime.getRuntime().exec(command);
        //消费正常日志
        this.clearLogStream(pcs.getInputStream(), String.format("%s-savepoint-success", DateUtil.now()));
        //消费错误日志
        this.clearLogStream(pcs.getErrorStream(), String.format("%s-savepoint-error", DateUtil.now()));

        int rs = pcs.waitFor();
        if (rs != 0) {
            throw new Exception("[savepointForPerYarn]执行savepoint失败 is error  rs=" + rs);
        }
    }

    /**
     * 清理pcs.waitFor()日志放置死锁
     *
     * @author zhuhuipei
     * @date 2021/3/28
     * @time 11:15
     */
    private void clearLogStream(InputStream stream, final String threadName) {
        WaitForPoolConfig.getInstance().getThreadPoolExecutor().execute(() -> {
                    BufferedInputStream reader = null;
                    try {
                        Thread.currentThread().setName(threadName);
                        reader = new BufferedInputStream(stream);
                        int bytesRead = 0;
                        byte[] buffer = new byte[1024];
                        while ((bytesRead = reader.read(buffer)) != -1) {
                            String result = new String(buffer, 0, bytesRead, SystemConstants.CODE_UTF_8);
                            log.info(result);
                        }
                    } catch (Exception e) {
                        log.error("threadName={}", threadName);
                    } finally {
                        this.close(reader, stream, "clearLogStream");
                    }
                }
        );
    }

    /**
     * 启动日志输出并且从日志中获取成功后的jobId
     *
     * @author zhuhuipei
     * @date 2021/3/28
     * @time 11:15
     */
    private String clearInfoLogStream(InputStream stream, StringBuilder localLog, Long jobRunLogId) {

        String appId = null;
        BufferedInputStream reader = null;
        try {
            long lastTime = System.currentTimeMillis();
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            reader = new BufferedInputStream(stream);
            while ((bytesRead = reader.read(buffer)) != -1) {
                String result = new String(buffer, 0, bytesRead, SystemConstants.CODE_UTF_8);
                log.info(result);
                if (StringUtils.isEmpty(appId) && result.contains(SystemConstant.QUERY_JOBID_KEY_WORD)) {
                    appId = result.replace(SystemConstant.QUERY_JOBID_KEY_WORD, SystemConstant.SPACE).trim();
                    localLog.append("[job-submitted-success] 解析得到的appId是:").append(appId).append(SystemConstant.LINE_FEED);
                }
                if (StringUtils.isEmpty(appId) && result.contains(SystemConstant.QUERY_JOBID_KEY_WORD_BACKUP)) {
                    appId = result.replace(SystemConstant.QUERY_JOBID_KEY_WORD_BACKUP, SystemConstant.SPACE).trim();
                    localLog.append("[Job has been submitted with JobID] 解析得到的appId是:").append(appId).append(SystemConstant.LINE_FEED);
                }

                localLog.append(result).append(SystemConstant.LINE_FEED);
                //每隔2s更新日志
                if (System.currentTimeMillis() >= lastTime + INTERVAL_TIME_TWO) {
                    jobRunLogService.updateLogById(localLog.toString(), jobRunLogId);
                    lastTime = System.currentTimeMillis();
                }
            }
            log.info("获取到的appId是 {}",appId);
            return appId;
        } catch (Exception e) {
            log.error("[clearInfoLogStream] is error", e);
            throw new RuntimeException("clearInfoLogStream is error");
        } finally {
            this.close(reader, stream, "clearInfoLogStream");

        }
    }

    /**
     * 关闭流
     *
     * @author zhuhuipei
     * @date 2021/3/28
     * @time 12:53
     */
    private void close(BufferedInputStream reader, InputStream stream, String typeName) {
        if (reader != null) {
            try {
                reader.close();
                log.info("[{}]关闭reader ", typeName);
            } catch (IOException e) {
                log.error("[{}] 关闭reader流失败 ", typeName, e);
            }
        }
        if (stream != null) {
            try {
                log.info("[{}]关闭stream ", typeName);
                stream.close();
            } catch (IOException e) {
                log.error("[{}] 关闭stream流失败 ", typeName, e);
            }
        }
        log.info("线程池状态: {}", WaitForPoolConfig.getInstance().getThreadPoolExecutor());
    }

}
