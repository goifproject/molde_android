package com.limefriends.molde.model.entity.cardNews;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;
import com.limefriends.molde.model.entity.ImageData;

public class CardNewsImageEntity implements ImageData {

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

    @Override
    public String getImageUrl() {
        return url;
    }
}
