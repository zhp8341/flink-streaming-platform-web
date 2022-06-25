package com.flink.streaming.web.controller.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-08-12
 * @time 21:20
 */
@Controller
@Slf4j
public class DefaultController {

  @RequestMapping("/")
  public String defaultUrl(ModelMap modelMap) {
    return "forward:/admin/listPage";
  }

}
