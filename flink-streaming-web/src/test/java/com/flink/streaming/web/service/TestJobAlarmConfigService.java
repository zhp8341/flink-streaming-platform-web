package com.flink.streaming.web.service;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.enums.AlarmTypeEnum;
import org.apache.commons.compress.utils.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/2/27
 * @time 18:03
 */
public class TestJobAlarmConfigService extends TestRun {

    @Autowired
    public JobAlarmConfigService jobAlarmConfigService;

    @Test
    public void insert() {

        List<AlarmTypeEnum> alarmTypeEnumList = Lists.newArrayList();
        alarmTypeEnumList.add(AlarmTypeEnum.DINGDING);
        alarmTypeEnumList.add(AlarmTypeEnum.CALLBACK_URL);

        jobAlarmConfigService.upSertBatchJobAlarmConfig(alarmTypeEnumList, 1L);
    }


    @Test
    public void find() {
        List<AlarmTypeEnum> list = jobAlarmConfigService.findByJobId(1L);
        Assert.assertNotNull(list);
        System.out.println(list);
    }

    @Test
    public void findByJobIdList() {
        List<Long> list= Lists.newArrayList();
        list.add(14L);
        list.add(11L);
        list.add(3L);
        Map<Long ,List<AlarmTypeEnum>>  map = jobAlarmConfigService.findByJobIdList(list);
        Assert.assertNotNull(map);
        System.out.println(map);
    }



}
