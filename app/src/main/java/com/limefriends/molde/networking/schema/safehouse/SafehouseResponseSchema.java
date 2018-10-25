package com.limefriends.molde.networking.schema.safehouse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SafehouseResponseSchema {

    @SerializedName("data")
    @Expose
    private List<SafehouseSchema> data = null;

    public List<SafehouseSchema> getData() {
        return data;
    }

    public void setData(List<SafehouseSchema> data) {
        this.data = data;
    }
}
