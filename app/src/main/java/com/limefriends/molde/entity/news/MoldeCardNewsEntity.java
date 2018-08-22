package com.limefriends.molde.entity.news;

import com.limefriends.molde.entity.comment.MoldeCommentEntity;

import java.util.List;

public class MoldeCardNewsEntity {

    private int newsId;
    private String postId;
    private String description;
    private String date;
    private List<MoldeCardNewsImageResponseInfoEntity> newsImg = null;

     private List<MoldeCommentEntity> comments;

    public List<MoldeCommentEntity> getComments() {
        return comments;
    }

    public void addComments(MoldeCommentEntity comment) {
        comments.add(comment);
    }

    public MoldeCardNewsEntity(int newsId, String postId, String description, String date,
                               List<MoldeCardNewsImageResponseInfoEntity> newsImg) {
        this.newsId = newsId;
        this.postId = postId;
        this.description = description;
        this.date = date;
        this.newsImg = newsImg;
    }

    public MoldeCardNewsEntity(int newsId, String postId, String description, String date,
                               List<MoldeCardNewsImageResponseInfoEntity> newsImg, List<MoldeCommentEntity> commentList) {
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

    public List<MoldeCardNewsImageResponseInfoEntity> getNewsImg() {
        return newsImg;
    }

    public void setNewsImg(List<MoldeCardNewsImageResponseInfoEntity> newsImg) {
        this.newsImg = newsImg;
    }
}
