package com.limefriends.molde.entity.comment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 2018-05-19.
 */

public class CommentEntity {

    private int commId;
    private String userId;
    private String userName;
    private int newsId;
    private String comment;
    private String commDate;

    private String newsTitle;
    private String newsImg;

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(String newsImg) {
        this.newsImg = newsImg;
    }

    public CommentEntity(String userId, String userName, int newsId, String comment, String commDate) {
        this.userId = userId;
        this.userName = userName;
        this.newsId = newsId;
        this.comment = comment;
        this.commDate = commDate;
    }

    public CommentEntity(int commId, String userId, String userName, int newsId, String comment, String commDate) {
        this.commId = commId;
        this.userId = userId;
        this.userName = userName;
        this.newsId = newsId;
        this.comment = comment;
        this.commDate = commDate;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommDate() {
        return commDate;
    }

    public void setCommDate(String commDate) {
        this.commDate = commDate;
    }
}
