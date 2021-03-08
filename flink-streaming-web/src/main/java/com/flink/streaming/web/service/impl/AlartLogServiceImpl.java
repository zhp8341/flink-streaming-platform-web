package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.mapper.AlarmLogMapper;
import com.flink.streaming.web.model.dto.AlartLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.entity.AlartLog;
import com.flink.streaming.web.model.param.AlartLogParam;
import com.flink.streaming.web.service.AlartLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-25
 * @time 21:43
 */
@Slf4j
@Service
public class AlartLogServiceImpl implements AlartLogService {


    @Autowired
    private AlarmLogMapper alarmLogMapper;

    @Override
    public void addAlartLog(AlartLogDTO alartLogDTO) {
        if (alartLogDTO == null) {
            return;
        }
        alarmLogMapper.insert(AlartLogDTO.toEntity(alartLogDTO));
    }


    @Override
    public AlartLogDTO findLogById(Long id) {
        return AlartLogDTO.toDTO(alarmLogMapper.selectByPrimaryKey(id));
    }

    @Override
    public PageModel<AlartLogDTO> queryAlartLog(AlartLogParam alartLogParam) {
        if (alartLogParam == null) {
            alartLogParam = new AlartLogParam();
        }
        PageHelper.startPage(alartLogParam.getPageNum(), alartLogParam.getPageSize(), YN.Y.getCode());

        Page<AlartLog> page = alarmLogMapper.selectByParam(alartLogParam);
        if (page == null) {
            return null;
        }
        PageModel<AlartLogDTO> pageModel = new PageModel<>();
        pageModel.setPageNum(page.getPageNum());
        pageModel.setPages(page.getPages());
        pageModel.setPageSize(page.getPageSize());
        pageModel.setTotal(page.getTotal());
        pageModel.addAll(AlartLogDTO.toListDTO(page.getResult()));
        return pageModel;

    }
}
