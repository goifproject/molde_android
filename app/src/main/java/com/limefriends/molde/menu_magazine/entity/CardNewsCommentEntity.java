package com.limefriends.molde.menu_magazine.entity;

/**
 * Created by joo on 2018. 4. 19..
 */

public class CardNewsCommentEntity {

    private int profileImg;
    private String userName;
    private String creDate;
    private String content;

    public CardNewsCommentEntity(int profileImg, String userName, String creDate, String content) {
        this.profileImg = profileImg;
        this.userName = userName;
        this.creDate = creDate;
        this.content = content;
    }

    // 프로필 사진이 없는 경우
    public CardNewsCommentEntity(String userName, String creDate, String content) {
        this.userName = userName;
        this.creDate = creDate;
        this.content = content;
    }


    public int getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(int profileImg) {
        this.profileImg = profileImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreDate() {
        return creDate;
    }

    public void setCreDate(String creDate) {
        this.creDate = creDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
