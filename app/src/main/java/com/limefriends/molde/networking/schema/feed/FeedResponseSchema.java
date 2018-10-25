package com.limefriends.molde.networking.schema.feed;

import java.util.ArrayList;

public class FeedResponseSchema {

    private ArrayList<FeedSchema> data;

    public ArrayList<FeedSchema> getData() {
        return data;
    }

    public void setData(ArrayList<FeedSchema> data) {
        this.data = data;
    }
}
