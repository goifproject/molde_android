package com.limefriends.molde.menu_feed.entity;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class MoldeFeedEntitiy implements Serializable{
    private String reportFeedAddress;
    private String reportFeedDetailAddress;
    private int reportFeedMarkerId;
    private String reportFeedThumbnail;
    private String reportFeedDate;
    private LatLng reportFeedLocation;

    public MoldeFeedEntitiy(String reportFeedAddress, String reportFeedDetailAddress,
                            int reportFeedMarkerId, String reportFeedThumbnail,
                            String reportFeedDate, LatLng reportFeedLocation) {
        this.reportFeedAddress = reportFeedAddress;
        this.reportFeedDetailAddress = reportFeedDetailAddress;
        this.reportFeedMarkerId = reportFeedMarkerId;
        this.reportFeedThumbnail = reportFeedThumbnail;
        this.reportFeedDate = reportFeedDate;
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
        return "addr : " + reportFeedAddress + ", marker : " + reportFeedMarkerId;
    }
}
