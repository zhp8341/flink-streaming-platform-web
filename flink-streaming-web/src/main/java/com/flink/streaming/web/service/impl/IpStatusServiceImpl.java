package com.flink.streaming.web.service.impl;

import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.common.util.IpUtil;
import com.flink.streaming.web.enums.IpStatusEnum;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.enums.YN;
import com.flink.streaming.web.mapper.IpStatusMapper;
import com.flink.streaming.web.model.entity.IpStatus;
import com.flink.streaming.web.service.IpStatusService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 02:26
 */
@Service
@Slf4j
public class IpStatusServiceImpl implements IpStatusService {

    @Autowired
    private IpStatusMapper ipStatusMapper;

    @Override
    public void registerIp() {
        String ip = IpUtil.getInstance().getLocalIP();
        if (StringUtils.isEmpty(ip)) {
            log.error(" get ip is null");
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }
        IpStatus ipStatus = new IpStatus();
        ipStatus.setIp(ip);
        ipStatus.setStatus(IpStatusEnum.START.getCode());
        ipStatus.setLastTime(new Date());
        ipStatus.setIsDeleted(YN.N.getValue());

        if (ipStatusMapper.selectByIp(ip) == null) {
            ipStatusMapper.insert(ipStatus);
        } else {
            ipStatusMapper.updateStatusByIp(ipStatus);
        }

    }

    @Override
    public void cancelIp() {
        String ip = IpUtil.getInstance().getLocalIP();
        IpStatus ipStatus = new IpStatus();
        ipStatus.setIp(ip);
        ipStatus.setStatus(IpStatusEnum.STOP.getCode());
        ipStatusMapper.updateStatusByIp(ipStatus);
    }

    @Override
    public void updateHeartbeatBylocalIp() {
        registerIp();
    }

    @Override
    public boolean isLeader() {

        try {
            String ip = IpUtil.getInstance().getLocalIP();
            if (StringUtils.isEmpty(ip)) {
                return false;
            }
            IpStatus ipStatus = ipStatusMapper.selectLastIp();
            if (ipStatus == null) {
                log.error("selectLastIp is null");
                return false;
            }
            if (ip.equals(ipStatus.getIp())) {
                return true;
            }
        } catch (Exception e) {
            log.error("isLeader is error", e);
        }

        return false;
    }
}
