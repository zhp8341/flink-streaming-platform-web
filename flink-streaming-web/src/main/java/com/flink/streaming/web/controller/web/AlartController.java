package com.flink.streaming.web.controller.web;

import com.flink.streaming.web.common.exceptions.BizException;
import com.flink.streaming.web.enums.SysConfigEnum;
import com.flink.streaming.web.enums.SysConfigEnumType;
import com.flink.streaming.web.model.dto.AlartLogDTO;
import com.flink.streaming.web.model.dto.PageModel;
import com.flink.streaming.web.model.param.AlartLogParam;
import com.flink.streaming.web.model.vo.AlartLogVO;
import com.flink.streaming.web.model.vo.PageVO;
import com.flink.streaming.web.model.vo.SystemConfigVO;
import com.flink.streaming.web.service.AlartLogService;
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
 * @date 2020-09-21
 * @time 01:52
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AlartController {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private AlartLogService alartLogService;

    @RequestMapping(value = "/upsertAlartConfig", method = {RequestMethod.POST})
    public String upsertAlartConfig(ModelMap modelMap, SystemConfigVO systemConfigVO) {
        if (systemConfigVO == null) {
            modelMap.put("message", "参数不能空");
            return this.alartConfig(modelMap);
        }
        try {
            systemConfigService.addOrUpdateConfigByKey(systemConfigVO.getKey(), systemConfigVO.getVal().trim());
        } catch (BizException biz) {
            modelMap.put("message", biz.getErrorMsg());
            log.warn("upsertAlartConfig is error ", biz);
            return this.alartConfig(modelMap);
        } catch (Exception e) {
            modelMap.put("message", "未知异常");
            log.error("upsertAlartConfig is error", e);
            return this.alartConfig(modelMap);
        }
        return "redirect:/admin/alartConfig";

    }


    @RequestMapping(value = "/alartLogList")
    public String queryAlartLogList(ModelMap modelMap, AlartLogParam alartLogParam){

        PageModel<AlartLogDTO> pageModel=alartLogService.queryAlartLog(alartLogParam);
        PageVO pageVO = new PageVO();
        pageVO.setPageNum(pageModel.getPageNum());
        pageVO.setPages(pageModel.getPages());
        pageVO.setPageSize(pageModel.getPageSize());
        pageVO.setTotal(pageModel.getTotal());

        modelMap.put("pageVO", pageVO);
        modelMap.put("alartLogParam", alartLogParam);
        modelMap.put("alartLogVOList", AlartLogVO.toListVO(pageModel.getResult()));
        modelMap.put("active", "list");
        return "screen/alart/listPage";
    }


    @RequestMapping(value = "/alartConfig")
    public String alartConfig(ModelMap modelMap) {
        modelMap.put("active", "synconfig");
        modelMap.put("sysConfigVOList", SysConfigEnum.getSysConfigEnumByType(SysConfigEnumType.ALART.name()));
        modelMap.put("systemConfigVOList", SystemConfigVO.toListVO(systemConfigService.getSystemConfig(SysConfigEnumType.ALART)));
        return "screen/alart/alart_config";
    }

}
