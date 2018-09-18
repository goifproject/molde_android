package com.limefriends.molde.entity.safehouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SafehouseResponseInfoEntityList {

    @SerializedName("data")
    @Expose
    private List<SafehouseResponseInfoEntity> data = null;

    public List<SafehouseResponseInfoEntity> getData() {
        return data;
    }

    public void setData(List<SafehouseResponseInfoEntity> data) {
        this.data = data;
    }
}
