package com.limefriends.molde.networking.schema.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReportedCommentSchema {

    @SerializedName("comm_rep_id")
    @Expose
    private int commRepId;
    @SerializedName("comm_id")
    @Expose
    private int commId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("comm_rep_date")
    @Expose
    private String commRepDate;

    public int getCommRepId() {
        return commRepId;
    }

    public void setCommRepId(int commRepId) {
        this.commRepId = commRepId;
    }

    public int getCommId() {
        return commId;
    }

    public void setCommId(int commId) {
        this.commId = commId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommRepDate() {
        return commRepDate;
    }

    public void setCommRepDate(String commRepDate) {
        this.commRepDate = commRepDate;
    }

}
