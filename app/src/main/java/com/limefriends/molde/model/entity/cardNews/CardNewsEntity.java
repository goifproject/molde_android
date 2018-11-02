package com.limefriends.molde.model.entity.cardNews;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.networking.schema.cardNews.CardNewsImageSchema;

import java.util.List;

public class CardNewsEntity implements Data {

    private int newsId;
    private String postId;
    private String description;
    private String date;
    private List<CardNewsImageEntity> newsImg;
    private List<CommentEntity> comments;

    public CardNewsEntity(int newsId, String postId, String description, String date,
                          List<CardNewsImageEntity> newsImg) {
        this.newsId = newsId;
        this.postId = postId;
        this.description = description;
        this.date = date;
        this.newsImg = newsImg;
    }

    public CardNewsEntity(int newsId, String postId, String description, String date,
                          List<CardNewsImageEntity> newsImg, List<CommentEntity> commentList) {
        this.newsId = newsId;
        this.postId = postId;
        this.description = description;
        this.date = date;
        this.newsImg = newsImg;
        this.comments = commentList;
    }

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

    public List<CardNewsImageEntity> getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(List<CardNewsImageEntity> newsImg) {
        this.newsImg = newsImg;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void addComments(CommentEntity comment) {
        comments.add(comment);
    }

    @Override
    public DataType getType() {
        return DataType.CARD_NEWS;
    }
}
