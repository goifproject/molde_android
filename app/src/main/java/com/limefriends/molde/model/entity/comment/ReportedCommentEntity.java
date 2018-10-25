package com.limefriends.molde.model.entity.comment;

public class ReportedCommentEntity {

    private int commRepId;
    private int commId;
    private String userId;
    private String commRepDate;

    public ReportedCommentEntity(int commRepId, int commId, String userId, String commRepDate) {
        this.commRepId = commRepId;
        this.commId = commId;
        this.userId = userId;
        this.commRepDate = commRepDate;
    }

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
