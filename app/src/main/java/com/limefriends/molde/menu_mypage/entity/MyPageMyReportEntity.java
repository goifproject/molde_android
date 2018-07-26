package com.limefriends.molde.menu_mypage.entity;


public class MyPageMyReportEntity {
    int myReport_map;
    String myReport_date;
    String myReport_location;

    public MyPageMyReportEntity(int myReport_map, String myReport_date, String myReport_location) {
        this.myReport_map = myReport_map;
        this.myReport_date = myReport_date;
        this.myReport_location = myReport_location;
    }

    public int getMyReport_map() {
        return myReport_map;
    }

    public void setMyReport_map(int myReport_map) {
        this.myReport_map = myReport_map;
    }

    public String getMyReport_date() {
        return myReport_date;
    }

    public void setMyReport_date(String myReport_date) {
        this.myReport_date = myReport_date;
    }

    public String getMyReport_location() {
        return myReport_location;
    }

    public void setMyReport_location(String myReport_location) {
        this.myReport_location = myReport_location;
    }
}

