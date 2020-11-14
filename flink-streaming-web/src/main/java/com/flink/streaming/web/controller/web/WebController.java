package com.flink.streaming.web.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-07
 * @time 22:00
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class WebController extends BaseController {


    @RequestMapping("/index")
    public String index(ModelMap modelMap) {
        String message = this.message();
        message = StringUtils.isEmpty(message) ? "" : message;
        modelMap.put("message", message);
        return "screen/login";
    }


    @RequestMapping("/qrcode")
    public String qrcode(ModelMap modelMap) {
        modelMap.put("open","qrcode");
        modelMap.put("active","qrcode");
        return "screen/qrcode";
    }

    private String message() {
        String message = this.getServletRequest().getParameter("message");
        try {
            return java.net.URLDecoder.decode(message, "UTF-8");
        } catch (Exception ex) {
        }
        return "error";
    }

}
