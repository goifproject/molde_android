package com.limefriends.molde.entity.feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoldeFeedEntity {

    private int repId;
    private String userName;
    private String userEmail;
    private String userId;
    private String repContents;
    private double repLat;
    private double repLon;
    private String repAddr;
    private String repDetailAddr;
    private String repDate;
    private List<MoldeFeedImageResponseInfoEntity> repImg = null;
    private int repState;

    public MoldeFeedEntity(String userName, String userEmail, String userId, String repContents, double repLat, double repLon, String repAddr, String repDetailAddr, String repDate, List<MoldeFeedImageResponseInfoEntity> repImg, int repState) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.repContents = repContents;
        this.repLat = repLat;
        this.repLon = repLon;
        this.repAddr = repAddr;
        this.repDetailAddr = repDetailAddr;
        this.repDate = repDate;
        this.repImg = repImg;
        this.repState = repState;
    }

    public MoldeFeedEntity(int repId, String userName, String userEmail, String userId, String repContents, double repLat, double repLon, String repAddr, String repDetailAddr, String repDate, List<MoldeFeedImageResponseInfoEntity> repImg, int repState) {
        this.repId = repId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.repContents = repContents;
        this.repLat = repLat;
        this.repLon = repLon;
        this.repAddr = repAddr;
        this.repDetailAddr = repDetailAddr;
        this.repDate = repDate;
        this.repImg = repImg;
        this.repState = repState;
    }

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

    public List<MoldeFeedImageResponseInfoEntity> getRepImg() {
        return repImg;
    }

    public void setRepImg(List<MoldeFeedImageResponseInfoEntity> repImg) {
        this.repImg = repImg;
    }

    public int getRepState() {
        return repState;
    }

    public void setRepState(int repState) {
        this.repState = repState;
    }
}
