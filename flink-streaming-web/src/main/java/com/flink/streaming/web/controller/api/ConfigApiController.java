package com.flink.streaming.web.controller.api;

import com.flink.streaming.web.common.RestResult;
import com.flink.streaming.web.exceptions.BizException;
import com.flink.streaming.web.model.vo.SystemConfigVO;
import com.flink.streaming.web.controller.web.BaseController;
import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ConfigApiController extends BaseController {


  @Autowired
  private SystemConfigService systemConfigService;

  @RequestMapping(value = "/upsertSynConfig", method = RequestMethod.POST)
  public RestResult<?> upsertSynConfig(String key, String val) {
    try {
      systemConfigService.addOrUpdateConfigByKey(key, val.trim());
    } catch (BizException biz) {
      log.warn("upsertSynConfig is error ", biz);
      return RestResult.error(biz.getMessage());
    } catch (Exception e) {
      log.error("upsertSynConfig is error", e);
      return RestResult.error(e.getMessage());
    }
    return RestResult.success();
  }


  @RequestMapping(value = "/deleteConfig", method = RequestMethod.POST)
  public RestResult<?> deleteConfig(String key) {
    try {
      systemConfigService.deleteConfigByKey(key);
    } catch (BizException biz) {
      log.warn("upsertSynConfig is error ", biz);
      return RestResult.error(biz.getMessage());
    } catch (Exception e) {
      log.error("upsertSynConfig is error", e);
      return RestResult.error(e.getMessage());
    }
    return RestResult.success();
  }

  /**
   * 查询系统配置列表
   *
   * @param modelMap
   * @return
   * @author wxj
   * @date 2021年12月16日 下午5:19:53
   * @version V1.0
   */
  @RequestMapping(value = "/sysConfig")
  public RestResult<?> sysConfig(ModelMap modelMap) {
    List<SystemConfigVO> list = SystemConfigVO
        .toListVO(systemConfigService.getSystemConfig(SysConfigEnumType.SYS));
    return RestResult.success(list);
  }

}
