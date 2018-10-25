package com.limefriends.molde.networking.schema.news;



import java.util.ArrayList;

public class CardNewsResponseSchema {

    private ArrayList<CardNewsSchema> data;

    public ArrayList<CardNewsSchema> getData() {
        return data;
    }

    public void setData(ArrayList<CardNewsSchema> data) {
        this.data = data;
    }
}
