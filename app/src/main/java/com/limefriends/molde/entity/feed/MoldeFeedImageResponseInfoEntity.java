package com.limefriends.molde.entity.feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoldeFeedImageResponseInfoEntity {

    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("filepath")
    @Expose
    private String filepath;
    @SerializedName("filesize")
    @Expose
    private String filesize;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

}
