package com.limefriends.molde.model.entity.safehouse;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;

public class SafehouseEntity implements Data {

    private int safeId;
    private String safeName;
    private String safeAddress;
    private String safePhone;
    private double safeLat;
    private double safeLon;
    private double distance;

    public SafehouseEntity(int safeId, String safeName, String safeAddress,
                           String safePhone, double safeLat, double safeLon, double distance) {
        this.safeId = safeId;
        this.safeName = safeName;
        this.safeAddress = safeAddress;
        this.safePhone = safePhone;
        this.safeLat = safeLat;
        this.safeLon = safeLon;
        this.distance = distance;
    }

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

    @Override
    public DataType getType() {
        return DataType.SAFE_HOUSE;
    }
}
