package com.limefriends.molde.entity.map.poi;


public class Poi {
    //POI 의  id
    private String id;
    //POI 의 name
    private String name;
    //POI 에 대한 전화번호
    private String telNo;
    //시설물 입구 위도 좌표
    private double frontLat;
    //시설물 입구 경도 좌표
    private double frontLon;
    //중심점 위도 좌표
    private double noorLat;
    //중심점 경도 좌표
    private double noorLon;
    //표출 주소 대분류명
    private String upperAddrName;
    //표출 주소 중분류명
    private String middleAddrName;
    //표출 주소 소분류명
    private String lowerAddrName;
    //표출 주소 세분류명
    private String detailAddrName;
    //본번
    private String firstNo;
    //부번
    private String secondNo;
    //도로명
    private String roadName;
    //건물번호 1
    private String firstBuildNo;
    //건물번호 2
    private String secondBuildNo;
    //업종 대분류명
    private String mlClass;
    //거리(km)
    private String radius;
    //업소명
    private String bizName;
    //시설목적
    private String upperBizName;
    //시설분류
    private String middleBizName;
    //시설이름 ex) 지하철역 병원 등
    private String lowerBizName;
    //상세 이름
    private String detailBizName;
    //길안내 요청 유무
    private String rpFlag;
    //주차 가능유무
    private String parkFlag;
    //POI 상세정보 유무
    private String detailInfoFlag;
    //소개 정보
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public double getFrontLat() {
        return frontLat;
    }

    public void setFrontLat(double frontLat) {
        this.frontLat = frontLat;
    }

    public double getFrontLon() {
        return frontLon;
    }

    public void setFrontLon(double frontLon) {
        this.frontLon = frontLon;
    }

    public double getNoorLat() {
        return noorLat;
    }

    public void setNoorLat(double noorLat) {
        this.noorLat = noorLat;
    }

    public double getNoorLon() {
        return noorLon;
    }

    public void setNoorLon(double noorLon) {
        this.noorLon = noorLon;
    }

    public String getUpperAddrName() {
        return upperAddrName;
    }

    public void setUpperAddrName(String upperAddrName) {
        this.upperAddrName = upperAddrName;
    }

    public String getMiddleAddrName() {
        return middleAddrName;
    }

    public void setMiddleAddrName(String middleAddrName) {
        this.middleAddrName = middleAddrName;
    }

    public String getLowerAddrName() {
        return lowerAddrName;
    }

    public void setLowerAddrName(String lowerAddrName) {
        this.lowerAddrName = lowerAddrName;
    }

    public String getDetailAddrName() {
        return detailAddrName;
    }

    public void setDetailAddrName(String detailAddrName) {
        this.detailAddrName = detailAddrName;
    }

    public String getMlClass() {
        return mlClass;
    }

    public void setMlClass(String mlClass) {
        this.mlClass = mlClass;
    }

    public String getFirstNo() {
        return firstNo;
    }

    public void setFirstNo(String firstNo) {
        this.firstNo = firstNo;
    }

    public String getSecondNo() {
        return secondNo;
    }

    public void setSecondNo(String secondNo) {
        this.secondNo = secondNo;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getFirstBuildNo() {
        return firstBuildNo;
    }

    public void setFirstBuildNo(String firstBuildNo) {
        this.firstBuildNo = firstBuildNo;
    }

    public String getSecondBuildNo() {
        return secondBuildNo;
    }

    public void setSecondBuildNo(String secondBuildNo) {
        this.secondBuildNo = secondBuildNo;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getUpperBizName() {
        return upperBizName;
    }

    public void setUpperBizName(String upperBizName) {
        this.upperBizName = upperBizName;
    }

    public String getMiddleBizName() {
        return middleBizName;
    }

    public void setMiddleBizName(String middleBizName) {
        this.middleBizName = middleBizName;
    }

    public String getLowerBizName() {
        return lowerBizName;
    }

    public void setLowerBizName(String lowerBizName) {
        this.lowerBizName = lowerBizName;
    }

    public String getDetailBizName() {
        return detailBizName;
    }

    public void setDetailBizName(String detailBizName) {
        this.detailBizName = detailBizName;
    }

    public String getRpFlag() {
        return rpFlag;
    }

    public void setRpFlag(String rpFlag) {
        this.rpFlag = rpFlag;
    }

    public String getParkFlag() {
        return parkFlag;
    }

    public void setParkFlag(String parkFlag) {
        this.parkFlag = parkFlag;
    }

    public String getDetailInfoFlag() {
        return detailInfoFlag;
    }

    public void setDetailInfoFlag(String detailInfoFlag) {
        this.detailInfoFlag = detailInfoFlag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
