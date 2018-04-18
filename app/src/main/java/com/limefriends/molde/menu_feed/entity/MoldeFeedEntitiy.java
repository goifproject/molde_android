package com.limefriends.molde.menu_feed.entity;

import android.graphics.Bitmap;

public class MoldeFeedEntitiy {
    //Bitmap reportFeedThumbnail;
    String reportFeedDate;
    String reportFeedAddress;
    String reportFeedDetailAddress;
    int markerId;

    public MoldeFeedEntitiy(String reportFeedDate,
                            String reportFeedAddress,
                            String reportFeedDetailAddress,
                            int markerId) {
        this.reportFeedDate = reportFeedDate;
        this.reportFeedAddress = reportFeedAddress;
        this.reportFeedDetailAddress = reportFeedDetailAddress;
        this.markerId = markerId;
    }

    public String getReportFeedDate() {
        return reportFeedDate;
    }

    public void setReportFeedDate(String reportFeedDate) {
        this.reportFeedDate = reportFeedDate;
    }

    public String getReportFeedAddress() {
        return reportFeedAddress;
    }

    public void setReportFeedAddress(String reportFeedAddress) {
        this.reportFeedAddress = reportFeedAddress;
    }

    public String getReportFeedDetailAddress() {
        return reportFeedDetailAddress;
    }

    public void setReportFeedDetailAddress(String reportFeedDetailAddress) {
        this.reportFeedDetailAddress = reportFeedDetailAddress;
    }

    public int getMarkerId() {
        return markerId;
    }

    public void setMarkerId(int markerId) {
        this.markerId = markerId;
    }
}
