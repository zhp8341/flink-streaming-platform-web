package com.flink.streaming.web.model.flink;

/**
 * @author earthchen
 * @date 2021/6/24
 **/
public class JobRunRequestInfo {

    private String entryClass;

    private String programArgs;

    private Integer parallelism;

    private String savepointPath;

    private String allowNonRestoredState;

    public String getEntryClass() {
        return entryClass;
    }

    public void setEntryClass(String entryClass) {
        this.entryClass = entryClass;
    }

    public String getProgramArgs() {
        return programArgs;
    }

    public void setProgramArgs(String programArgs) {
        this.programArgs = programArgs;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public String getSavepointPath() {
        return savepointPath;
    }

    public void setSavepointPath(String savepointPath) {
        this.savepointPath = savepointPath;
    }

    public String getAllowNonRestoredState() {
        return allowNonRestoredState;
    }

    public void setAllowNonRestoredState(String allowNonRestoredState) {
        this.allowNonRestoredState = allowNonRestoredState;
    }

    @Override
    public String toString() {
        return "JobRunInfo{" +
                "entryClass='" + entryClass + '\'' +
                ", programArgs='" + programArgs + '\'' +
                ", parallelism=" + parallelism +
                ", savepointPath='" + savepointPath + '\'' +
                ", allowNonRestoredState='" + allowNonRestoredState + '\'' +
                '}';
    }
}
