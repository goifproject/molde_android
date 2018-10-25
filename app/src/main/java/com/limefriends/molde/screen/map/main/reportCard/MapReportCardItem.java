package com.limefriends.molde.screen.map.main.reportCard;


public class MapReportCardItem {

    private int repId;
    private String repContent;
    private String detailAddress;
    private int status;
    private String thumbnailUrl;
    private String date;

    public MapReportCardItem(String repContent, String detailAddress, int status, int repId, String date) {
        this.repContent = repContent;
        this.detailAddress = detailAddress;
        this.status = status;
        this.repId = repId;
        this.date = date;
    }

    // 썸네일이 사용될 경우
    public MapReportCardItem(String repContent, String detailAddress, int status, int repId, String date, String thumbnailUrl) {
        this.repContent = repContent;
        this.detailAddress = detailAddress;
        this.status = status;
        this.repId = repId;
        this.date = date;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getRepContent() {
        return repContent;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public int getStatus() {return status;}

    public int getRepId() {
        return repId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
