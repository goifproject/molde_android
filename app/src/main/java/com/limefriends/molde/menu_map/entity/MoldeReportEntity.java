package com.limefriends.molde.menu_map.entity;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MoldeReportEntity implements Serializable {
    private String reportUserId;
    private String reportUserName;
    private List<File> reportImageList;
    private String reportContent;
    private String reportAddress;
    private String reportDetailAddress;
    private String reportEmail;
    private String reportLat;
    private String reportLng;
    private Date reportDate;

    public MoldeReportEntity(String reportUserId, String reportUserName, List<File> reportImageList,
                             String reportContent, String reportAddress, String reportDetailAddress, String reportEmail,
                             String reportLat, String reportLng, Date reportDate) {
        this.reportUserId = reportUserId;
        this.reportUserName = reportUserName;
        this.reportImageList = reportImageList;
        this.reportContent = reportContent;
        this.reportAddress = reportAddress;
        this.reportEmail = reportEmail;
        this.reportLat = reportLat;
        this.reportLng = reportLng;
        this.reportDate = reportDate;
    }

    public String getReportUserId() {
        return reportUserId;
    }

    public void setReportUserId(String reportUserId) {
        this.reportUserId = reportUserId;
    }

    public String getReportUserName() {
        return reportUserName;
    }

    public void setReportUserName(String reportUserName) {
        this.reportUserName = reportUserName;
    }

    public List<File> getReportImageList() {
        return reportImageList;
    }

    public void setReportImageList(List<File> reportImageList) {
        this.reportImageList = reportImageList;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
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

    public String getReportEmail() {
        return reportEmail;
    }

    public void setReportEmail(String reportEmail) {
        this.reportEmail = reportEmail;
    }

    public String getReportLat() {
        return reportLat;
    }

    public void setReportLat(String reportLat) {
        this.reportLat = reportLat;
    }

    public String getReportLng() {
        return reportLng;
    }

    public void setReportLng(String reportLng) {
        this.reportLng = reportLng;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }
}
