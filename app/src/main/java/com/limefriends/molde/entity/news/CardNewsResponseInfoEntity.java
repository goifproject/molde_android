package com.limefriends.molde.entity.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CardNewsResponseInfoEntity {

    @SerializedName("news_id")
    @Expose
    private int newsId;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("news_img")
    @Expose
    private List<CardNewsImageResponseInfoEntity> newsImg = null;

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CardNewsImageResponseInfoEntity> getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(List<CardNewsImageResponseInfoEntity> newsImg) {
        this.newsImg = newsImg;
    }
}
