package com.limefriends.molde.menu_feed.entity;

import java.util.ArrayList;

public class MoldeFeedResponseInfoEntityList {
    private ArrayList<MoldeFeedResponseInfoEntity> data;

    public ArrayList<MoldeFeedResponseInfoEntity> getFeed() {
        return data;
    }

    public void setFeed(ArrayList<MoldeFeedResponseInfoEntity> data) {
        this.data = data;
    }
}