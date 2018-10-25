package com.limefriends.molde.networking.schema.scrap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScrapSchema {

    @SerializedName("scrap_id")
    @Expose(deserialize = false)
    private int scrapId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("news_id")
    @Expose(deserialize = false)
    private int newsId;

    /**
     * post 용
     */
    @SerializedName("card_news_id")
    @Expose(serialize = false)
    private String cardNewId;

    /**
     * delete 용
     */
    @SerializedName("card_news_scrap_id")
    @Expose(serialize = false)
    private String cardNewsScrapId;

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

//    @Override
//    public String toString() {
//        return new ToStringBuilder(this).append("scrapId", scrapId).append("userId", userId).append("newsId", newsId).toString();
//    }
//
//    @Override
//    public int hashCode() {
//        return new HashCodeBuilder().append(scrapId).append(newsId).append(userId).toHashCode();
//    }
//
//    @Override
//    public boolean equals(Object other) {
//        if (other == this) {
//            return true;
//        }
//        if ((other instanceof MyPageMyScrapResponseInfoEntity) == false) {
//            return false;
//        }
//        ScrapSchema rhs = ((ScrapSchema) other);
//        return new EqualsBuilder().append(scrapId, rhs.scrapId).append(newsId, rhs.newsId).append(userId, rhs.userId).isEquals();
//    }


}
