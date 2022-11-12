package com.flink.streaming.web.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.web.common.FlinkYarnRestUriConstants;
import com.flink.streaming.web.common.SystemConstants;
import com.flink.streaming.web.common.util.FileUtils;
import com.flink.streaming.web.common.util.HttpServiceCheckerUtil;
import com.flink.streaming.web.config.LocalCache;
import com.flink.streaming.web.enums.DeployModeEnum;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.enums.SysErrorEnum;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.mapper.SystemConfigMapper;
import com.flink.streaming.web.model.dto.SystemConfigDTO;
import com.flink.streaming.web.model.entity.SystemConfig;
import com.flink.streaming.web.service.SystemConfigService;
import java.net.URL;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

  @Autowired
  private LocalCache localCache;


  @Override
  public void addOrUpdateConfigByKey(String key, String value) {

    this.checkParam(key, value);
    if (SysConfigEnum.FLINK_HOME.equals(SysConfigEnum.getSysConfigEnum(key))) {
      FileUtils.createSqlHome(value);
    }
    SystemConfig systemConfig = systemConfigMapper.selectConfigByKey(key);
    if (systemConfig == null) {
      systemConfigMapper.insert(new SystemConfig(key, value.trim()));
    } else {
      systemConfigMapper.updateByKey(new SystemConfig(key, value.trim()));
    }

  }

  @Override
  public List<SystemConfigDTO> getSystemConfig(SysConfigEnumType sysConfigEnumType) {
    return SystemConfigDTO.toListDTO(systemConfigMapper.selectAllConfig(sysConfigEnumType != null
        ? sysConfigEnumType.name() : null));
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
    String urlHa = this.getSystemConfigByKey(SysConfigEnum.YARN_RM_HTTP_ADDRESS.getKey());
    if (StringUtils.isEmpty(urlHa)) {
      throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_YARN_RM_HTTP_ADDRESS);
    }

    return getActiveYarnUrl(urlHa);


  }

  @Override
  public String getFlinkAddress(DeployModeEnum deployModeEnum) {
    try {
      String url = this.getFlinkHttpAddress(deployModeEnum);
      URL address = new URL(url);
      String host = address.getHost();
      Integer port = address.getPort() == -1 ? 80 : address.getPort();
      return host + ":" + port;
    } catch (Exception e) {
      log.error("getFlinkAddress is error", e);
    }
    return null;
  }

  @Override
  public String getFlinkHttpAddress(DeployModeEnum deployModeEnum) {

    switch (deployModeEnum) {
      case LOCAL:
        String urlLocal = this.getSystemConfigByKey(SysConfigEnum.FLINK_REST_HTTP_ADDRESS.getKey());
        if (StringUtils.isEmpty(urlLocal)) {
          throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_REST_HTTP_ADDRESS);
        }
        if (HttpServiceCheckerUtil.checkUrlConnect(urlLocal)) {
          return urlLocal.trim();
        }
        throw new BizException("网络异常 url=" + urlLocal);
      case STANDALONE:
        String urlHA = this.getSystemConfigByKey(SysConfigEnum.FLINK_REST_HA_HTTP_ADDRESS.getKey());
        if (StringUtils.isEmpty(urlHA)) {
          throw new BizException(SysErrorEnum.SYSTEM_CONFIG_IS_NULL_FLINK_REST_HA_HTTP_ADDRESS);
        }
        String[] urls = urlHA.split(SystemConstant.SEMICOLON);
        for (String http : urls) {
          if (HttpServiceCheckerUtil.checkUrlConnect(http)) {
            return http.trim();
          }
        }
        throw new BizException("网络异常 url=" + urlHA);
      default:
        throw new BizException("不支持该模式");
    }


  }

  @Override
  public String getFlinkUrl(DeployModeEnum deployModeEnum) {
    String url = localCache.get(deployModeEnum.name());
    if (StringUtils.isNotEmpty(url)) {
      return url;
    }
    try {
      switch (deployModeEnum) {
        case LOCAL:
        case STANDALONE:
          url = getFlinkHttpAddress(deployModeEnum);
          break;
        case YARN_APPLICATION:
        case YARN_PER:
          url = getYarnRmHttpAddress();
          break;
        default:
          throw new BizException("不支持该模式=" + deployModeEnum.name());
      }
      localCache.put(deployModeEnum.name(), url);
    } catch (Exception e) {
      log.error("getFlinkUrl is error", e);
      localCache.put(deployModeEnum.name(), "null");
    }
    return url;
  }

  @Override
  public String getUploadJarsPath() {
    String path = this
        .getSystemConfigByKey(SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.getKey());
    if (StringUtils.isEmpty(path)) {
      throw new BizException("请先去系统设置界面设置Flink管理平台目录(即flink_streaming_platform_web)");
    }
    return path + SystemConstant.VIRGULE + SystemConstant.JAR_ROOT_PATH;
  }

  @Override
  public boolean isExist(String key) {
    String value = this.getSystemConfigByKey(key);
    if (StringUtils.isEmpty(value)) {
      return false;
    }
    return true;
  }

  @Override
  public boolean autoSavepoint() {
    String value = this.getSystemConfigByKey(SysConfigEnum.AUTO_SAVEPOINT.getKey());
    if (StringUtils.isEmpty(value)) {
      return true;
    }
    log.info("autoSavepoint ={}", value);
    return Boolean.parseBoolean(value);
  }


  private void checkParam(String key, String value) {
    if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
      throw new BizException(SysErrorEnum.PARAM_IS_NULL);
    }
    SysConfigEnum sysConfigEnum = SysConfigEnum.getSysConfigEnum(key);

    if (SysConfigEnum.YARN_RM_HTTP_ADDRESS.equals(sysConfigEnum)
        || SysConfigEnum.FLINK_REST_HTTP_ADDRESS.equals(sysConfigEnum)
        || SysConfigEnum.FLINK_REST_HA_HTTP_ADDRESS.equals(sysConfigEnum)) {
      if (!StrUtil.endWith(value, SystemConstants.SLASH)) {
        throw new BizException("必须以/结尾");
      }
      if (!StrUtil.startWith(value, SystemConstants.HTTP_KEY)) {
        throw new BizException("必须以http或者https开头");
      }
    }
    if (SysConfigEnum.DINGDING_ALARM_URL.equals(sysConfigEnum)) {
      if (!StrUtil.startWith(value, SystemConstants.HTTP_KEY)) {
        throw new BizException("必须以http或者https开头");
      }
    }

    this.checkUrlValid(sysConfigEnum, value);

    if (SysConfigEnum.FLINK_HOME.equals(sysConfigEnum)) {
      if (!StrUtil.endWith(value, SystemConstants.SLASH)) {
        throw new BizException("必须以/结尾");
      }
      if (!StrUtil.startWith(value, SystemConstants.SLASH)) {
        throw new BizException("必须以/开头");
      }
    }
    if (SysConfigEnum.FLINK_STREAMING_PLATFORM_WEB_HOME.equals(sysConfigEnum)) {
      if (!StrUtil.startWith(value, SystemConstants.SLASH)) {
        throw new BizException("必须以/开头");
      }
      if (!StrUtil.endWith(value, SystemConstants.SLASH)) {
        throw new BizException("必须以/结尾");
      }

    }
  }

  private void checkUrlValid(SysConfigEnum sysConfigEnum, String url) {
    switch (sysConfigEnum) {
      case FLINK_REST_HTTP_ADDRESS:
      case DINGDING_ALARM_URL:
        if (!HttpServiceCheckerUtil.checkUrlConnect(url)) {
          throw new BizException("网络异常 url=" + url);
        }
        break;
      case YARN_RM_HTTP_ADDRESS:
      case FLINK_REST_HA_HTTP_ADDRESS:
        String[] urls = url.split(SystemConstant.SEMICOLON);
        for (String http : urls) {
          if (!HttpServiceCheckerUtil.checkUrlConnect(http)) {
            throw new BizException("网络异常 url=" + http);
          }
        }
        break;
      default:
        break;
    }

  }

  private String getActiveYarnUrl(String urlHa) {
    String[] urls = urlHa.split(SystemConstant.SEMICOLON);
    for (String http : urls) {
      try {
        String url = com.flink.streaming.web.common.util.HttpUtil
            .buildUrl(http, FlinkYarnRestUriConstants.URI_YARN_INFO);
        String request = HttpUtil.get(url, HttpServiceCheckerUtil.TIMEOUTMILLSECONDS);
        if (StringUtils.isNotEmpty(request)) {
          JSONObject jsonObject = (JSONObject) JSON.parse(request);
          String haState = jsonObject.getJSONObject("clusterInfo").get("haState").toString();
          if ("ACTIVE".equalsIgnoreCase(haState)) {
            return http;
          }
        }
      } catch (Exception e) {
        log.error("单个http异常={}", http, e);
      }
    }
    throw new BizException("网络异常 url=" + urlHa);
  }

}
