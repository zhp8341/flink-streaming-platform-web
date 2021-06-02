package com.flink.streaming.web.common.util;

import com.flink.streaming.web.common.MessageConstants;
import com.flink.streaming.web.exceptions.BizException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/5/5
 * @time 10:46
 */
public class LinuxInfoUtil {

    public static String  loginName(){
        String userName=System.getProperty("user.name");
        if (StringUtils.isNotEmpty(userName)){
            return userName;
        }
        throw  new BizException(MessageConstants.MESSAGE_011);

    }
}
