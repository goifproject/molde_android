package com.limefriends.molde.entity.feedResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResultResponseInfoEntity {

    @SerializedName("result_id")
    @Expose
    private int resultId;
    @SerializedName("rep_id")
    @Expose
    private int repId;
    @SerializedName("result_date")
    @Expose
    private int resultDate;
    @SerializedName("result_img")
    @Expose
    private List<FeedResultImgResponseInfoEntity> resultImg = null;

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public int getRepId() {
        return repId;
    }

    public void setRepId(int repId) {
        this.repId = repId;
    }

    public int getResultDate() {
        return resultDate;
    }

    public void setResultDate(int resultDate) {
        this.resultDate = resultDate;
    }

    public List<FeedResultImgResponseInfoEntity> getResultImg() {
        return resultImg;
    }

    public void setResultImg(List<FeedResultImgResponseInfoEntity> resultImg) {
        this.resultImg = resultImg;
    }
}
