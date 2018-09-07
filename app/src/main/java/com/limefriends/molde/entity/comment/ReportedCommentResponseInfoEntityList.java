package com.limefriends.molde.entity.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportedCommentResponseInfoEntityList {

    @SerializedName("data")
    @Expose
    private List<ReportedCommentResponseInfoEntity> data = null;

    public List<ReportedCommentResponseInfoEntity> getData() {
        return data;
    }

    public void setData(List<ReportedCommentResponseInfoEntity> data) {
        this.data = data;
    }

}
