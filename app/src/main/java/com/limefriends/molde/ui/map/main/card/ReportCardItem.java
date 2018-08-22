package com.limefriends.molde.ui.map.main.card;


public class ReportCardItem {

    private int repId;
    private String title;
    private String text;
    private int status;

    // makeRandomReport 를 위한 임시 생성자
    public ReportCardItem(String title, String text, int status) {
        this.title = title;
        this.text = text;
        this.status = status;
        this.repId = repId;
    }

    public ReportCardItem(String title, String text, int status, int repId) {
        this.title = title;
        this.text = text;
        this.status = status;
        this.repId = repId;
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
}
