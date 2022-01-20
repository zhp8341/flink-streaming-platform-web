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

    public final static String LINE_FEED = "\n";

    public final static String SPACE = "";

    public final static String VIRGULE = "/";

    public static final int DEFAULT_PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;


    public final static String JARVERSION = "lib/flink-streaming-core-1.4.0.RELEASE.jar";


    public static final String QUERY_JOBID_KEY_WORD = "job-submitted-success:";

    public static final String QUERY_JOBID_KEY_WORD_BACKUP = "Job has been submitted with JobID";



}
