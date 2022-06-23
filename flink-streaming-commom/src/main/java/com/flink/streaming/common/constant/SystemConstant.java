package com.flink.streaming.common.constant;

import java.util.regex.Pattern;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 * @time 23:03
 */
public class SystemConstant {

    public static final  String COMMENT_SYMBOL = "--";

    public static final  String SEMICOLON = ";";

    public static final  String LINE_FEED = "\n";

    public static final  String SPACE = "";

    public static final  String VIRGULE = "/";

    public static final int DEFAULT_PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;


    public static final  String JARVERSION = "lib/flink-streaming-core-1.4.0.RELEASE.jar";


    public static final String QUERY_JOBID_KEY_WORD = "job-submitted-success:";

    public static final String QUERY_JOBID_KEY_WORD_BACKUP = "Job has been submitted with JobID";



}
