package com.limefriends.molde.menu_feed.entity;

import java.util.List;

public class MoldeFeedResponseInfoEntity {
    private String rep_id;
    private String user_name;
    private String user_email;
    private String user_id;
    private String rep_contents;
    private String rep_lat;
    private String rep_lon;
    private String rep_addr;
    private String rep_detail_addr;
    private String rep_date;
    private List<String> rep_img;
    private int rep_state;

    public MoldeFeedResponseInfoEntity(String rep_id, String user_name, String user_email, String user_id,
                                       String rep_contents, String rep_lat, String rep_lon, String rep_addr,
                                       String rep_detail_addr, String rep_date, List<String> rep_img, int rep_state) {
        this.rep_id = rep_id;
        this.user_name = user_name;
        this.user_email = user_email;
        this.user_id = user_id;
        this.rep_contents = rep_contents;
        this.rep_lat = rep_lat;
        this.rep_lon = rep_lon;
        this.rep_addr = rep_addr;
        this.rep_detail_addr = rep_detail_addr;
        this.rep_date = rep_date;
        this.rep_img = rep_img;
        this.rep_state = rep_state;
    }

    public String getRep_id() {
        return rep_id;
    }

    public void setRep_id(String rep_id) {
        this.rep_id = rep_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRep_contents() {
        return rep_contents;
    }

    public void setRep_contents(String rep_contents) {
        this.rep_contents = rep_contents;
    }

    public String getRep_lat() {
        return rep_lat;
    }

    public void setRep_lat(String rep_lat) {
        this.rep_lat = rep_lat;
    }

    public String getRep_lon() {
        return rep_lon;
    }

    public void setRep_lon(String rep_lon) {
        this.rep_lon = rep_lon;
    }

    public String getRep_addr() {
        return rep_addr;
    }

    public void setRep_addr(String rep_addr) {
        this.rep_addr = rep_addr;
    }

    public String getRep_detail_addr() {
        return rep_detail_addr;
    }

    public void setRep_detail_addr(String rep_detail_addr) {
        this.rep_detail_addr = rep_detail_addr;
    }

    public String getRep_date() {
        return rep_date;
    }

    public void setRep_date(String rep_date) {
        this.rep_date = rep_date;
    }

    public List<String> getRep_img() {
        return rep_img;
    }

    public void setRep_img(List<String> rep_img) {
        this.rep_img = rep_img;
    }

    public int getRep_state() {
        return rep_state;
    }

    public void setRep_state(int rep_state) {
        this.rep_state = rep_state;
    }

    @Override
    public String toString() {
        return "state : " + rep_state + ", name : " + user_name + ", address : " + rep_addr + ", detail : " + rep_detail_addr;
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
  "rep_addr" : "신고 주소",
  "rep_detail_addr" : "신고 상세주소"
 }
*/