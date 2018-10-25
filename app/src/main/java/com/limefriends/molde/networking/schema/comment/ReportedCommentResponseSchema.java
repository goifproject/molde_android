package com.limefriends.molde.networking.schema.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportedCommentResponseSchema {

    @SerializedName("data")
    @Expose
    private List<ReportedCommentSchema> data = null;

    public List<ReportedCommentSchema> getData() {
        return data;
    }

    public void setData(List<ReportedCommentSchema> data) {
        this.data = data;
    }

}
