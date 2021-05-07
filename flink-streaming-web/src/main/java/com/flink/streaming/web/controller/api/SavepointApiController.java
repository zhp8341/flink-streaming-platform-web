package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.service.SavepointBackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-21
 * @time 01:52
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class SavepointApiController {

    @Autowired
    private SavepointBackupService savepointBackupService;


    @RequestMapping(value = "/addSavepoint")
    public RestResult<String> addSavepoint(Long jobConfigId, String savepointPath) {
        try {
            savepointBackupService.insertSavepoint(jobConfigId, savepointPath, new Date());
        } catch (BizException e) {
            log.error("addSavepoint is error jobConfigId={},savepointPath={}", jobConfigId, savepointPath, e);
            return RestResult.error(e.getCode(), e.getErrorMsg());
        } catch (Exception e) {
            log.error("addSavepoint  error jobConfigId={},savepointPath={}", jobConfigId, savepointPath, e);
            return RestResult.error(SysErrorEnum.ADD_SAVEPOINT_ERROR);
        }
        return RestResult.success();
    }

}
