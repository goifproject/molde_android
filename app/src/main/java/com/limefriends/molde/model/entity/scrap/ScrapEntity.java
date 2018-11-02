package com.limefriends.molde.model.entity.scrap;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;

public class ScrapEntity implements Data {

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

    @Override
    public DataType getType() {
        return DataType.SCRAP;
    }
}
