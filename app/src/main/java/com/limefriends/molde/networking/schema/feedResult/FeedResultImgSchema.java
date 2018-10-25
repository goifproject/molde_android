package com.limefriends.molde.networking.schema.feedResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedResultImgSchema {

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
