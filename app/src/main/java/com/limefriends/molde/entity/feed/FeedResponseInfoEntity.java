package com.limefriends.molde.entity.feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResponseInfoEntity {

    @SerializedName("rep_id")
    @Expose
    private int repId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("rep_contents")
    @Expose
    private String repContents;
    @SerializedName("rep_lat")
    @Expose
    private double repLat;
    @SerializedName("rep_lon")
    @Expose
    private double repLon;
    @SerializedName("rep_addr")
    @Expose
    private String repAddr;
    @SerializedName("rep_detail_addr")
    @Expose
    private String repDetailAddr;
    @SerializedName("rep_date")
    @Expose
    private String repDate;
    @SerializedName("rep_img")
    @Expose
    private List<FeedImageResponseInfoEntity> repImg = null;
    @SerializedName("rep_state")
    @Expose
    private int repState;

    public int getRepId() {
        return repId;
    }

    public void setRepId(int repId) {
        this.repId = repId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRepContents() {
        return repContents;
    }

    public void setRepContents(String repContents) {
        this.repContents = repContents;
    }

    public double getRepLat() {
        return repLat;
    }

    public void setRepLat(double repLat) {
        this.repLat = repLat;
    }

    public double getRepLon() {
        return repLon;
    }

    public void setRepLon(double repLon) {
        this.repLon = repLon;
    }

    public String getRepAddr() {
        return repAddr;
    }

    public void setRepAddr(String repAddr) {
        this.repAddr = repAddr;
    }

    public String getRepDetailAddr() {
        return repDetailAddr;
    }

    public void setRepDetailAddr(String repDetailAddr) {
        this.repDetailAddr = repDetailAddr;
    }

    public String getRepDate() {
        return repDate;
    }

    public void setRepDate(String repDate) {
        this.repDate = repDate;
    }

    public List<FeedImageResponseInfoEntity> getRepImg() {
        return repImg;
    }

    public void setRepImg(List<FeedImageResponseInfoEntity> repImg) {
        this.repImg = repImg;
    }

    public int getRepState() {
        return repState;
    }

    public void setRepState(int repState) {
        this.repState = repState;
    }

}
