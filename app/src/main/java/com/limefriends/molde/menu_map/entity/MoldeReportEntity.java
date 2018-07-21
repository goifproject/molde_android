package com.limefriends.molde.menu_map.entity;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MoldeReportEntity implements Serializable {
    private String userId;
    private String userName;
    private String userEmail;
    private int reportState;
    private List<File> reportImageList;
    private String reportContent;
    private String reportAddress;
    private String reportDetailAddress;
    private String reportLat;
    private String reportLng;
    private Date reportDate;

    public MoldeReportEntity(String userId, String userName, int reportState, List<File> reportImageList,
                             String reportContent, String reportAddress, String reportDetailAddress,
                             String userEmail, String reportLat, String reportLng, Date reportDate) {
        this.userId = userId;
        this.userName = userName;
        this.reportState = reportState;
        this.reportImageList = reportImageList;
        this.reportContent = reportContent;
        this.reportAddress = reportAddress;
        this.reportDetailAddress = reportDetailAddress;
        this.userEmail = userEmail;
        this.reportLat = reportLat;
        this.reportLng = reportLng;
        this.reportDate = reportDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String reportUserId) {
        this.userId = reportUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String reportUserName) {
        this.userName = reportUserName;
    }

    public int getReportState() {
        return reportState;
    }

    public void setReportState(int reportState) {
        this.reportState = reportState;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
