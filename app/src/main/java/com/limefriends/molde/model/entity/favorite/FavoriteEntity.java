package com.limefriends.molde.model.entity.favorite;

import java.io.Serializable;

public class FavoriteEntity implements Serializable {

    private int favId;
    private String userId;
    private String favName;
    private String favAddr;
    private double favLat;
    private double favLon;

    private String content;
    private boolean isActive;

    public FavoriteEntity(String userId, String favName, String favAddr, double favLat, double favLon) {
        this.userId = userId;
        this.favName = favName;
        this.favAddr = favAddr;
        this.favLat = favLat;
        this.favLon = favLon;
    }

    public FavoriteEntity(int favId, String userId, String favName, String favAddr, double favLat, double favLon) {
        this.favId = favId;
        this.userId = userId;
        this.favName = favName;
        this.favAddr = favAddr;
        this.favLat = favLat;
        this.favLon = favLon;
    }

    public FavoriteEntity(int favId, String userId, String favName, String favAddr, double favLat, double favLon, boolean isActive) {
        this.favId = favId;
        this.userId = userId;
        this.favName = favName;
        this.favAddr = favAddr;
        this.favLat = favLat;
        this.favLon = favLon;
        this.isActive = isActive;
    }

    public int getFavId() {
        return favId;
    }

    public void setFavId(int favId) {
        this.favId = favId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFavName() {
        return favName;
    }

    public void setFavName(String favName) {
        this.favName = favName;
    }

    public String getFavAddr() {
        return favAddr;
    }

    public void setFavAddr(String favAddr) {
        this.favAddr = favAddr;
    }

    public double getFavLat() {
        return favLat;
    }

    public void setFavLat(double favLat) {
        this.favLat = favLat;
    }

    public double getFavLon() {
        return favLon;
    }

    public void setFavLon(double favLon) {
        this.favLon = favLon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
