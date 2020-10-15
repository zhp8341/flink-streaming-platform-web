package com.flink.streaming.web.service;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.model.dto.SavepointBackupDTO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-17
 * @time 20:47
 */
public class TestSavepointBackupService extends TestRun {

    @Autowired
    private SavepointBackupService savepointBackupService;


    @Test
    public void insert(){
        for (int i = 0; i <10 ; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            savepointBackupService.insertSavepoint(1L,"hdfs:///xxxx",new Date());
        }
    }

    @Test
    public void lasterHistory10(){
        List<SavepointBackupDTO>  list=savepointBackupService.lasterHistory10(1L);
        System.out.println(list);
    }
}
