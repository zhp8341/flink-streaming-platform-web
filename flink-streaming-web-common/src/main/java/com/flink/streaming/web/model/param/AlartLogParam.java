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
public class AlartLogParam extends PageParam {

    private Long jobConfigId;


    /**
     * 1:钉钉
     */
    private Integer type;

    /**
     * 1:成功 0:失败
     */
    private Integer status;

}
