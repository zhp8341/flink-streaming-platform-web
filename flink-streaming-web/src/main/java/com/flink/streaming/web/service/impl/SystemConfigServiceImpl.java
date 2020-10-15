package com.flink.streaming.web.service.impl;

import cn.hutool.core.util.StrUtil;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.common.util.FileUtils;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.mapper.SystemConfigMapper;
import com.flink.streaming.web.model.dto.SystemConfigDTO;
import com.flink.streaming.web.model.entity.SystemConfig;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-20
 * @time 01:06
 */
@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;


    @Override
    public void addOrUpdateConfigByKey(String key, String value) {

        this.checkParam(key, value);

        if (SysConfigEnum.FLINK_HOME.equals(SysConfigEnum.getSysConfigEnum(key))) {
            FileUtils.createSqlHome(value);
        }

        SystemConfig systemConfig = systemConfigMapper.selectConfigByKey(key);
        if (systemConfig == null) {
            systemConfigMapper.insert(new SystemConfig(key, value));
        } else {
            systemConfigMapper.updateByKey(new SystemConfig(key, value));
        }

    }

    @Override
    public List<SystemConfigDTO> getSystemConfig(SysConfigEnumType sysConfigEnumType) {
        return SystemConfigDTO.toListDTO(systemConfigMapper.selectAllConfig(sysConfigEnumType!=null?sysConfigEnumType.name():null));
    }

    @Override
    public void deleteConfigByKey(String key) {
        systemConfigMapper.deleteByKey(key);
    }

    @Override
    public String getSystemConfigByKey(String key) {
        List<SystemConfigDTO> list = this.getSystemConfig(null);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return SystemConfigDTO.toMap(list).get(key);
    }

    @Override
    public String getYarnRmHttpAddress() {
        String http = this.getSystemConfigByKey(SysConfigEnum.YARN_RM_HTTP_ADDRESS.getKey());
        if (StringUtils.isEmpty(http)) {
            throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_YARN_RM_HTTP_ADDRESS);
        }
        return http;
    }

    @Override
    public boolean isExist(String key) {
        String value = this.getSystemConfigByKey(key);
        if (StringUtils.isEmpty(value)){
            return false;
        }
        return true;
    }



    private void checkParam(String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            throw new BizException(SysErrorEnum.PARAM_IS_NULL);
        }

        if (SysConfigEnum.YARN_RM_HTTP_ADDRESS.equals(SysConfigEnum.getSysConfigEnum(key))) {
            if (!StrUtil.endWith(value, SystemConstants.SLASH)) {
                throw new BizException("必须以/结尾");
            }
            if (!StrUtil.startWith(value, SystemConstants.HTTP_KEY)){
                throw new BizException("必须以http或者https开头");
            }
        }
        if (SysConfigEnum.FLINK_HOME.equals(SysConfigEnum.getSysConfigEnum(key))) {
            if (!StrUtil.endWith(value, SystemConstants.SLASH)) {
                throw new BizException("必须以/结尾");
            }
            if (!StrUtil.startWith(value, SystemConstants.SLASH)) {
                throw new BizException("必须以/开头");
            }
        }
        if (SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.equals(SysConfigEnum.getSysConfigEnum(key))) {
            if (!StrUtil.startWith(value, SystemConstants.SLASH)) {
                throw new BizException("必须以/开头");
            }
            if (!StrUtil.endWith(value, SystemConstants.SLASH)) {
                throw new BizException("必须以/结尾");
            }

        }


    }


}
