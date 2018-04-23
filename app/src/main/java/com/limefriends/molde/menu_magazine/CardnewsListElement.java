package com.limefriends.molde.menu_magazine;

public class CardnewsListElement {
    private int thumbnail;
    private String title;

    public CardnewsListElement(int thumbnail, String title) {
        this.thumbnail = thumbnail;
        this.title = title;
    }


    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
