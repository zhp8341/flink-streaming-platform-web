package com.flink.streaming.web.rpc.model;

/**
 * @author earthchen
 * @date 2021/6/24
 **/
public class JobRunInfo {

    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public String toString() {
        return "JobRunInfo{" +
                "jobId='" + jobId + '\'' +
                '}';
    }
}
