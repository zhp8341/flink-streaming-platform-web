package com.flink.streaming.web.model.param;

import com.flink.streaming.web.model.page.PageParam;
import lombok.Data;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-15
 * @time 02:04
 */
@Data
public class UploadFileParam extends PageParam {

  private String fileName;

}
