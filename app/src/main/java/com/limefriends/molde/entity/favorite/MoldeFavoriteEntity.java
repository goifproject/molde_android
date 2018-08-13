package com.limefriends.molde.entity.favorite;

public class MoldeFavoriteEntity {

    private int favId;
    private String userId;
    private String favAddr;
    private double favLat;
    private double favLon;

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

}
