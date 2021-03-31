package com.flink.streaming.web.common;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-09-18
 * @time 23:55
 */
public class FlinkYarnRestUriConstants {

    public final static String URI_PROXY = "proxy/";

    public final static String URI_JOBS = "jobs";

    public final static String URI_YARN_CANCEL = "/yarn-cancel";


    public final static String URI_YARN_CHECKPOINT = "/checkpoints";

    public final static String URI_YARN_INFO = "ws/v1/cluster/info";

    public final static String URI_YARN_OVERVIEW = "#/overview";

    public final static String URI_YARN_JOB_OVERVIEW = "#/job/%s/overview";

    public final static String URI_CHECKPOINTS_JOB = "jobs/%s/checkpoints";



    public static String getUriJobsForYarn(String appId) {
        return rootUriForYarn(appId) + URI_JOBS;
    }

    public static String getUriOverviewForYarn(String appId) {
        return rootUriForYarn(appId) + URI_YARN_OVERVIEW;
    }

    public static String getUriJobsForStandalone(String appId) {
        return URI_JOBS + "/" + appId;
    }


    public static String getUriCancelForYarn(String appId, String jobId) {
        return getUriJobsForYarn(appId) + "/" + jobId + URI_YARN_CANCEL;
    }

    public static String getUriCancelForStandalone(String jobId) {
        return URI_JOBS + "/" + jobId + URI_YARN_CANCEL;
    }

    public static String getUriCheckpointForYarn(String appId, String jobId) {
        return getUriJobsForYarn(appId) + "/" + jobId + URI_YARN_CHECKPOINT;
    }

    public static String rootUriForYarn(String appId) {
        return String.format(URI_PROXY + "%s/", appId);
    }

    public static String getUriCheckpoints(String appId) {
        return String.format(URI_CHECKPOINTS_JOB, appId);
    }


    public static void main(String[] args) {
        System.out.println(FlinkYarnRestUriConstants.getUriJobsForYarn("xxxx"));
        System.out.println(FlinkYarnRestUriConstants.getUriCheckpoints("xxxx"));
    }

}
