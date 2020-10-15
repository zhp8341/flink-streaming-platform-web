package com.flink.streaming.web.service;

import com.flink.streaming.web.base.TestRun;
import com.flink.streaming.web.enums.AlartLogStatusEnum;
import com.flink.streaming.web.enums.AlartLogTypeEnum;
import com.flink.streaming.web.model.dto.AlartLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 19:27
 */
public class TestAlartLogService extends TestRun {

    @Autowired
    private AlartLogService alartLogService;

    @Test
    public void addAlartLog() {

        for (int i = 0; i <20 ; i++) {
            AlartLogDTO alartLogDTO = new AlartLogDTO();
            alartLogDTO.setJobConfigId(2L);
            alartLogDTO.setAlartLogTypeEnum(AlartLogTypeEnum.DINGDING);
            alartLogDTO.setAlartLogStatusEnum(AlartLogStatusEnum.SUCCESS);
            alartLogDTO.setMessage("单测成功");
            alartLogService.addAlartLog(alartLogDTO);


            AlartLogDTO alartLogDTO2 = new AlartLogDTO();
            alartLogDTO2.setJobConfigId(2L);
            alartLogDTO2.setAlartLogTypeEnum(AlartLogTypeEnum.DINGDING);
            alartLogDTO2.setAlartLogStatusEnum(AlartLogStatusEnum.FAIL);
            alartLogDTO2.setMessage("单测失败");
            alartLogDTO2.setFailLog("xxx失败");
            alartLogService.addAlartLog(alartLogDTO2);
        }


    }


    @Test
    public void findLogById(){
        AlartLogDTO alartLogDTO= alartLogService.findLogById(2L);
        System.out.println(alartLogDTO);
    }


    @Test
    public void queryAlartLog(){
        PageModel<AlartLogDTO> pageModel= alartLogService.queryAlartLog(null);
        System.out.println(pageModel);
    }
}
