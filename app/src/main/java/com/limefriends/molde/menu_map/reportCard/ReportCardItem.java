package com.limefriends.molde.menu_map.reportCard;


public class ReportCardItem {

    private String text;
    private String title;

    public ReportCardItem(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}
