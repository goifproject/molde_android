package com.limefriends.molde.networking.schema.favorite;

import java.util.ArrayList;

public class FavoriteResponseSchema {

    private ArrayList<FavoriteSchema> data;

    public ArrayList<FavoriteSchema> getData() {
        return data;
    }

    public void setData(ArrayList<FavoriteSchema> data) {
        this.data = data;
    }


}
