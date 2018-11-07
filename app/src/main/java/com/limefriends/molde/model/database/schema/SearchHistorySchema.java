package com.limefriends.molde.model.database.schema;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "search")
public class SearchHistorySchema {

    @PrimaryKey(autoGenerate = true)
    private int uId;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String bizName;
    @ColumnInfo
    private String mainAddress;
    @ColumnInfo
    private String streetAddress;
    @ColumnInfo
    private double lat;
    @ColumnInfo
    private double lng;

    public SearchHistorySchema(int uId, String name, String bizName, String mainAddress, String streetAddress, double lat, double lng) {
        this.uId = uId;
        this.name = name;
        this.bizName = bizName;
        this.mainAddress = mainAddress;
        this.streetAddress = streetAddress;
        this.lat = lat;
        this.lng = lng;
    }

    public int getUId() {
        return uId;
    }

    public void setUId(int uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(String mainAddress) {
        this.mainAddress = mainAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
