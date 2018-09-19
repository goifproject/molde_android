package com.limefriends.molde.entity.feedResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResultResponseInfoEntityList {

    @SerializedName("data")
    @Expose
    private List<FeedResultResponseInfoEntity> data = null;

    public List<FeedResultResponseInfoEntity> getData() {
        return data;
    }

    public void setData(List<FeedResultResponseInfoEntity> data) {
        this.data = data;
    }
}
