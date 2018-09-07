package com.limefriends.molde.entity.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportedCommentResponseInfoEntity {

    @SerializedName("comm_rep_id")
    @Expose
    private Integer commRepId;
    @SerializedName("comm_id")
    @Expose
    private Integer commId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("comm_rep_date")
    @Expose
    private Integer commRepDate;

    public Integer getCommRepId() {
        return commRepId;
    }

    public void setCommRepId(Integer commRepId) {
        this.commRepId = commRepId;
    }

    public Integer getCommId() {
        return commId;
    }

    public void setCommId(Integer commId) {
        this.commId = commId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCommRepDate() {
        return commRepDate;
    }

    public void setCommRepDate(Integer commRepDate) {
        this.commRepDate = commRepDate;
    }

}
