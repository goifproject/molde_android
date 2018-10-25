package com.limefriends.molde.networking.schema.feedResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResultResponseSchema {

    @SerializedName("data")
    @Expose
    private List<FeedResultSchema> data = null;

    public List<FeedResultSchema> getData() {
        return data;
    }

    public void setData(List<FeedResultSchema> data) {
        this.data = data;
    }
}
