package com.limefriends.molde.model.entity.feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;
import com.limefriends.molde.model.entity.ImageData;

public class FeedImageEntity implements ImageData {

    private String filename;
    private String filepath;
    private String filesize;

    public FeedImageEntity(String filename, String filepath, String filesize) {
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
    }

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

    @Override
    public String getImageUrl() {
        return filepath;
    }
}
