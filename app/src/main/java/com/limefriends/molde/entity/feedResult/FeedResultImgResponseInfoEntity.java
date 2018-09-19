package com.limefriends.molde.entity.feedResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedResultImgResponseInfoEntity {

    @SerializedName("filepath")
    @Expose
    private String filepath;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

}
