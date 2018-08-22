package com.limefriends.molde.entity.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardNewsImageResponseInfoEntity {

    @SerializedName("page_num")
    @Expose
    private int pageNum;
    @SerializedName("url")
    @Expose
    private String url;

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
