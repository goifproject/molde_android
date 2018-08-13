package com.limefriends.molde.ui.menu_map.entity;

import android.graphics.Bitmap;
import java.util.Date;

public class MoldeReportMapEntitiy {
    //Bitmap reportThumbnailImage;
    private String reportDate;
    private String reportAddress;
    private String reportDetailAddress;

    public MoldeReportMapEntitiy(String reportDate, String reportAddress, String reportDetailAddress) {
        this.reportDate = reportDate;
        this.reportAddress = reportAddress;
        this.reportDetailAddress = reportDetailAddress;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportAddress() {
        return reportAddress;
    }

    public void setReportAddress(String reportAddress) {
        this.reportAddress = reportAddress;
    }

    public String getReportDetailAddress() {
        return reportDetailAddress;
    }

    public void setReportDetailAddress(String reportDetailAddress) {
        this.reportDetailAddress = reportDetailAddress;
    }
}