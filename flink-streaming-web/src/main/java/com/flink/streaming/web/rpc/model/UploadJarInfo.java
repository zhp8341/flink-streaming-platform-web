package com.flink.streaming.web.rpc.model;

/**
 * @author earthchen
 * @date 2021/6/24
 **/
public class UploadJarInfo {

    private String filename;

    private String status;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UploadJarInfo{" +
                "filename='" + filename + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getJarId() {
        String[] ans = filename.split("/");
        if (ans.length == 0) {
            return null;
        }
        return ans[ans.length - 1];
    }


}
