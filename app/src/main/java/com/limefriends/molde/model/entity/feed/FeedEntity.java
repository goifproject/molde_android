package com.limefriends.molde.model.entity.feed;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;
import com.limefriends.molde.networking.schema.feed.FeedImageSchema;

import java.util.List;

public class FeedEntity implements Data {

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
    private List<FeedImageEntity> repImg = null;
    private int repState;

    public FeedEntity(String repContent, String detailAddress, int status, int repId, String date, List<FeedImageEntity> thumbnailUrl) {
        this.repContents = repContent;
        this.repDetailAddr = detailAddress;
        this.repState = status;
        this.repId = repId;
        this.repDate = date;
        this.repImg = thumbnailUrl;
    }

    public FeedEntity(String userName, String userEmail, String userId,
                      String repContents, double repLat, double repLon,
                      String repAddr, String repDetailAddr, String repDate,
                      List<FeedImageEntity> repImg, int repState) {
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

    public FeedEntity(int repId, String userName, String userEmail, String userId,
                      String repContents, double repLat, double repLon, String repAddr,
                      String repDetailAddr, String repDate, List<FeedImageEntity> repImg, int repState) {
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

    public List<FeedImageEntity> getRepImg() {
        return repImg;
    }

    public void setRepImg(List<FeedImageEntity> repImg) {
        this.repImg = repImg;
    }

    public int getRepState() {
        return repState;
    }

    public void setRepState(int repState) {
        this.repState = repState;
    }

    @Override
    public DataType getType() {
        return DataType.FEED;
    }
}
