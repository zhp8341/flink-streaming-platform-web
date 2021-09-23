package com.flink.streaming.web.common;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 23:55
 */
public class FlinkRestUriConstants {

    public final static String JARS = "jars";
    public static String getUriJobsUploadForStandalone() {
        return JARS + "/upload";
    }

    public static String getUriJobsRunForStandalone() {
        return JARS + "/upload";
    }

    public static String getUriJobsRunForStandalone(String jobId) {
        return JARS + "/" + jobId + "/run";
    }


}
