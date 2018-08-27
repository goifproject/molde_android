package com.limefriends.molde.ui.map.main.reportCard;


public class MapReportCardItem {

    private int repId;
    private String title;
    private String text;
    private int status;
    private String thumbnailUrl;

    public MapReportCardItem(String title, String text, int status, int repId) {
        this.title = title;
        this.text = text;
        this.status = status;
        this.repId = repId;
    }

    // 썸네일이 사용될 경우
    public MapReportCardItem(String title, String text, int status, int repId, String thumbnailUrl) {
        this.title = title;
        this.text = text;
        this.status = status;
        this.repId = repId;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
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
}
