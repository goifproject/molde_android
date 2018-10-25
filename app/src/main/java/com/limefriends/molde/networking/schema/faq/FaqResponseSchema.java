package com.limefriends.molde.networking.schema.faq;


import java.util.ArrayList;

public class FaqResponseSchema {

    private ArrayList<FaqSchema> data;

    public ArrayList<FaqSchema> getData() {
        return data;
    }

    public void setData(ArrayList<FaqSchema> data) {
        this.data = data;
    }
}
