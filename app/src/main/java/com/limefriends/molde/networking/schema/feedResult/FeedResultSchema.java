package com.limefriends.molde.networking.schema.feedResult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FeedResultSchema {

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
    private List<FeedResultImgSchema> resultImg = null;

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

    public List<FeedResultImgSchema> getResultImg() {
        return resultImg;
    }

    public void setResultImg(List<FeedResultImgSchema> resultImg) {
        this.resultImg = resultImg;
    }
}
