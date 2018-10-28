package com.limefriends.molde.model.entity.cardNews;

public class CardNewsImageEntity {

    private int pageNum;
    private String url;

    public CardNewsImageEntity(int pageNum, String url) {
        this.pageNum = pageNum;
        this.url = url;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
