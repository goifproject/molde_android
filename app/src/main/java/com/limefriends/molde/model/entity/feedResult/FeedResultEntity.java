package com.limefriends.molde.model.entity.feedResult;

import com.limefriends.molde.model.entity.Data;
import com.limefriends.molde.model.entity.DataType;
import com.limefriends.molde.networking.schema.feedResult.FeedResultImgSchema;

import java.util.List;

public class FeedResultEntity implements Data {

    private int resultId;
    private int repId;
    private int resultDate;
    private List<FeedResultImgSchema> resultImg = null;

    public FeedResultEntity(int resultId, int repId, int resultDate, List<FeedResultImgSchema> resultImg) {
        this.resultId = resultId;
        this.repId = repId;
        this.resultDate = resultDate;
        this.resultImg = resultImg;
    }

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

    @Override
    public DataType getType() {
        return DataType.FEED_RESULT;
    }
}
