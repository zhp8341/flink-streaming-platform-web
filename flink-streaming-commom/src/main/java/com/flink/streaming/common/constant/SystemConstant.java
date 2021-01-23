package com.flink.streaming.common.constant;

import java.util.regex.Pattern;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 23:03
 */
public class SystemConstant {

    public final static String COMMENT_SYMBOL = "--";

    public final static String SEMICOLON = ";";

    public final static String LINE_FEED= "\n";

    public static final int DEFAULT_PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
}
