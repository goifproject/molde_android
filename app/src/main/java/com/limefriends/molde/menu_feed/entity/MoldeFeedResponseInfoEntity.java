package com.limefriends.molde.menu_feed.entity;

public class MoldeFeedResponseInfoEntity {
    private String rep_id;
    private String rep_nm;
    private String rep_contents;
    private String rep_state;
    private String rep_addr;
    private String rep_lat;
    private String rep_lon;
    private String rep_date;
    private String rep_detail_addr;
    //private String rep_img;


    public MoldeFeedResponseInfoEntity(String rep_id, String rep_nm, String rep_contents,
                                       String rep_state, String rep_addr, String rep_lat,
                                       String rep_lon, String rep_date, String rep_detail_addr) {
        this.rep_id = rep_id;
        this.rep_nm = rep_nm;
        this.rep_contents = rep_contents;
        this.rep_state = rep_state;
        this.rep_addr = rep_addr;
        this.rep_lat = rep_lat;
        this.rep_lon = rep_lon;
        this.rep_date = rep_date;
        this.rep_detail_addr = rep_detail_addr;
    }

    public String getRep_id() {
        return rep_id;
    }

    public String getRep_nm() {
        return rep_nm;
    }

    public String getRep_contents() {
        return rep_contents;
    }

    public String getRep_state() {
        return rep_state;
    }

    public String getRep_addr() {
        return rep_addr;
    }

    public String getRep_lat() {
        return rep_lat;
    }

    public String getRep_lon() {
        return rep_lon;
    }

    public String getRep_date() {
        return rep_date;
    }

    public String getRep_detail_addr() {
        return rep_detail_addr;
    }

    @Override
    public String toString() {
        return "state : " + rep_state + ", name : " + rep_nm + ", address : " + rep_addr;
    }
}



/*
 {
  "rep_id" : "신고 내역 아이디",
  "rep_nm" : "신고 내역 이름",
  "rep_contents" : "신고 내역 내용",
  "rep_state" : "신고 내역 상태",
  "rep_lat" : "위도",
  "rep_lon" : "경도" ,
  "rep_img" : "이미지 데이터",
  "rep_addr" : "신고 주소",
  "rep_detail_addr" : "신고 상세주소"
 }
*/