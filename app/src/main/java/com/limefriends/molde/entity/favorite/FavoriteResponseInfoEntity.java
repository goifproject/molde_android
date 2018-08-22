package com.limefriends.molde.entity.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FavoriteResponseInfoEntity {

    @SerializedName("fav_id")
    @Expose
    private int favId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("fav_name")
    @Expose
    private String favName;
    @SerializedName("fav_addr")
    @Expose
    private String favAddr;
    @SerializedName("fav_lat")
    @Expose
    private double favLat;
    @SerializedName("fav_lon")
    @Expose
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

}