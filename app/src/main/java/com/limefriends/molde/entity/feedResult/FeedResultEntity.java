package com.limefriends.molde.entity.feedResult;

import java.util.List;

public class FeedResultEntity {

    private int resultId;
    private int repId;
    private int resultDate;
    private List<FeedResultImgResponseInfoEntity> resultImg = null;

    public FeedResultEntity(int resultId, int repId, int resultDate, List<FeedResultImgResponseInfoEntity> resultImg) {
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

    public List<FeedResultImgResponseInfoEntity> getResultImg() {
        return resultImg;
    }

    public void setResultImg(List<FeedResultImgResponseInfoEntity> resultImg) {
        this.resultImg = resultImg;
    }

}
