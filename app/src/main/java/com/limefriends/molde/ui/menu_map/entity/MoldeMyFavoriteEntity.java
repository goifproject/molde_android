package com.limefriends.molde.ui.menu_map.entity;

import java.io.Serializable;

public class MoldeMyFavoriteEntity implements Serializable {
    private String mapLat;
    private String mapLng;
    private String myFavoriteTitle;
    private String myFavoriteInfo;

    private boolean myFavoriteActive;

    public MoldeMyFavoriteEntity() { }

    public MoldeMyFavoriteEntity(String mapLat, String mapLng,
                                 String myFavoriteTitle, String myFavoriteInfo,
                                 boolean myFavoriteActive) {
        this.mapLat = mapLat;
        this.mapLng = mapLng;
        this.myFavoriteTitle = myFavoriteTitle;
        this.myFavoriteInfo = myFavoriteInfo;
        this.myFavoriteActive = myFavoriteActive;
    }

    public String getMapLat() {
        return mapLat;
    }

    public void setMapLat(String mapLat) {
        this.mapLat = mapLat;
    }

    public String getMapLng() {
        return mapLng;
    }

    public void setMapLng(String mapLng) {
        this.mapLng = mapLng;
    }

    public String getMyFavoriteTitle() {
        return myFavoriteTitle;
    }

    public void setMyFavoriteTitle(String myFavoriteTitle) {
        this.myFavoriteTitle = myFavoriteTitle;
    }

    public String getMyFavoriteInfo() {
        return myFavoriteInfo;
    }

    public void setMyFavoriteInfo(String myFavoriteInfo) {
        this.myFavoriteInfo = myFavoriteInfo;
    }

    public boolean isMyFavoriteActive() {
        return myFavoriteActive;
    }

    public void setMyFavoriteActive(boolean myFavoriteActive) {
        this.myFavoriteActive = myFavoriteActive;
    }

    @Override
    public String toString() {
        return "제목 : " + myFavoriteTitle + ", 정보 : " + myFavoriteInfo;
    }
}
