package com.limefriends.molde.entity.scrap;

/**
 * Created by user on 2018-05-19.
 */

public class ScrapEntity {

    private int scrapId;
    private String userId;
    private int newsId;

    public ScrapEntity(int scrapId, String userId, int newsId) {
        this.scrapId = scrapId;
        this.userId = userId;
        this.newsId = newsId;
    }

    public int getScrapId() {
        return scrapId;
    }

    public void setScrapId(int scrapId) {
        this.scrapId = scrapId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }
}
