package com.limefriends.molde.networking.schema.scrap;




import java.util.ArrayList;

public class ScrapResponseSchema {

    private ArrayList<ScrapSchema> data;

    public ArrayList<ScrapSchema> getData() {
        return data;
    }

    public void setData(ArrayList<ScrapSchema> data) {
        this.data = data;
    }
}
