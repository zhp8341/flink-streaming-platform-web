package com.flink.streaming.web.controller.web;

import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.model.vo.SystemConfigVO;
import com.flink.streaming.web.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class SystemConfigController {


    @Autowired
    private SystemConfigService systemConfigService;


    @RequestMapping(value = "/sysConfig")
    public String sysConfig(ModelMap modelMap) {
        modelMap.put("active", "synconfig");
        modelMap.put("open", "system");
        modelMap.put("sysConfigVOList", SysConfigEnum.getSysConfigEnumByType(SysConfigEnumType.SYS.name()));
        modelMap.put("systemConfigVOList", SystemConfigVO.toListVO(systemConfigService.getSystemConfig(SysConfigEnumType.SYS)));

        if (!modelMap.containsKey("message")) {
            for (String key : SysConfigEnum.getMustKey()) {
                if (!systemConfigService.isExist(key)) {
                    modelMap.put("message", "请正确填写：" + SysConfigEnum.getSysConfigEnum(key).getDesc());
                }
            }
        }
        return "screen/sysconfig";
    }


    @RequestMapping(value = "/upsertSynConfig", method = {RequestMethod.POST})
    public String upsertSynConfig(ModelMap modelMap, SystemConfigVO systemConfigVO) {
        if (systemConfigVO == null) {
            modelMap.put("message", "参数不能空");
            return this.sysConfig(modelMap);
        }
        try {
            systemConfigService.addOrUpdateConfigByKey(systemConfigVO.getKey(), systemConfigVO.getVal().trim());
        } catch (BizException biz) {
            modelMap.put("message", biz.getErrorMsg());
            log.warn("upsertSynConfig is error ", biz);
            return this.sysConfig(modelMap);
        } catch (Exception e) {
            modelMap.put("message", "未知异常");
            log.error("upsertSynConfig is error", e);
            return this.sysConfig(modelMap);
        }
        return "redirect:/admin/sysConfig";
    }


}
