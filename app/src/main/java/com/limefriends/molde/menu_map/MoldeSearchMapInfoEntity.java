package com.limefriends.molde.menu_map;


public class MoldeSearchMapInfoEntity {
    private String title;
    private String address;
    private String mapLat;
    private String mapLng;

    public MoldeSearchMapInfoEntity(String title, String address, String mapLat, String mapLng) {
        this.title = title;
        this.address = address;
        this.mapLat = mapLat;
        this.mapLng = mapLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMapLat() {
        return mapLat;
    }

    public void setMapLat(String centLat) {
        this.mapLat = centLat;
    }

    public String getMapLng() {
        return mapLng;
    }

    public void setMapLng(String centLng) {
        this.mapLng = centLng;
    }
}
