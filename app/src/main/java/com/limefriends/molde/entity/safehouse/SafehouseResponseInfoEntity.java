package com.limefriends.molde.entity.safehouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SafehouseResponseInfoEntity {

    @SerializedName("safe_id")
    @Expose
    private int safeId;
    @SerializedName("safe_name")
    @Expose
    private String safeName;
    @SerializedName("safe_address")
    @Expose
    private String safeAddress;
    @SerializedName("safe_phone")
    @Expose
    private String safePhone;
    @SerializedName("safe_lat")
    @Expose
    private double safeLat;
    @SerializedName("safe_lon")
    @Expose
    private double safeLon;
    @SerializedName("distance")
    @Expose
    private double distance;

    public int getSafeId() {
        return safeId;
    }

    public void setSafeId(int safeId) {
        this.safeId = safeId;
    }

    public String getSafeName() {
        return safeName;
    }

    public void setSafeName(String safeName) {
        this.safeName = safeName;
    }

    public String getSafeAddress() {
        return safeAddress;
    }

    public void setSafeAddress(String safeAddress) {
        this.safeAddress = safeAddress;
    }

    public String getSafePhone() {
        return safePhone;
    }

    public void setSafePhone(String safePhone) {
        this.safePhone = safePhone;
    }

    public double getSafeLat() {
        return safeLat;
    }

    public void setSafeLat(double safeLat) {
        this.safeLat = safeLat;
    }

    public double getSafeLon() {
        return safeLon;
    }

    public void setSafeLon(double safeLon) {
        this.safeLon = safeLon;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
