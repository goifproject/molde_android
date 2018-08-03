package com.limefriends.molde.menu_feed.entity;

import com.google.android.gms.maps.model.LatLng;

public class MoldeFeedEntity {
    private String reportFeedAddress;
    private String reportFeedDetailAddress;
    private int reportFeedMarkerId;
    private String reportFeedThumbnail;
    private String reportFeedDate;
    private LatLng reportFeedLocation;


    public MoldeFeedEntity(String reportFeedAddress, String reportFeedDetailAddress,
                           int reportFeedMarkerId, String reportFeedThumbnail,
                           String reportFeedDate, LatLng reportFeedLocation) {
        this.reportFeedAddress = reportFeedAddress;
        this.reportFeedDetailAddress = reportFeedDetailAddress;
        this.reportFeedMarkerId = reportFeedMarkerId;
        this.reportFeedThumbnail = reportFeedThumbnail;
        this.reportFeedDate = reportFeedDate;
        this.reportFeedLocation = reportFeedLocation;
    }

    public void setReportFeedAddress(String reportFeedAddress) {
        this.reportFeedAddress = reportFeedAddress;
    }

    public void setReportFeedDetailAddress(String reportFeedDetailAddress) {
        this.reportFeedDetailAddress = reportFeedDetailAddress;
    }

    public void setReportFeedMarkerId(int reportFeedMarkerId) {
        this.reportFeedMarkerId = reportFeedMarkerId;
    }

    public void setReportFeedThumbnail(String reportFeedThumbnail) {
        this.reportFeedThumbnail = reportFeedThumbnail;
    }

    public void setReportFeedDate(String reportFeedDate) {
        this.reportFeedDate = reportFeedDate;
    }

    public void setReportFeedLocation(LatLng reportFeedLocation) {
        this.reportFeedLocation = reportFeedLocation;
    }

    public String getReportFeedAddress() {
        return reportFeedAddress;
    }

    public String getReportFeedDetailAddress() {
        return reportFeedDetailAddress;
    }

    public int getReportFeedMarkerId() {
        return reportFeedMarkerId;
    }

    public String getReportFeedThumbnail() {
        return reportFeedThumbnail;
    }

    public String getReportFeedDate() {
        return reportFeedDate;
    }

    public LatLng getReportFeedLocation() {
        return reportFeedLocation;
    }

    @Override
    public String toString() {
        return "addr : " + reportFeedAddress + ", marker : " + reportFeedMarkerId + ", Loc : " + reportFeedLocation;
    }
}
