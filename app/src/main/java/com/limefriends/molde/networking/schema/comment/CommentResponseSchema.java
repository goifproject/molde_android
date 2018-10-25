package com.limefriends.molde.networking.schema.comment;


import java.util.ArrayList;

public class CommentResponseSchema {

    private ArrayList<CommentSchema> data;

    public ArrayList<CommentSchema> getData() {
        return data;
    }

    public void setData(ArrayList<CommentSchema> data) {
        this.data = data;
    }
}
