package com.limefriends.molde.entity.comment;

public class ReportedCommentEntity {

    private Integer commRepId;
    private Integer commId;
    private String userId;
    private Integer commRepDate;

    public ReportedCommentEntity(Integer commRepId, Integer commId, String userId, Integer commRepDate) {
        this.commRepId = commRepId;
        this.commId = commId;
        this.userId = userId;
        this.commRepDate = commRepDate;
    }

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
