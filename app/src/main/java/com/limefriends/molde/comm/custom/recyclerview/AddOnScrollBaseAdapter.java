package com.limefriends.molde.comm.custom.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public abstract class AddOnScrollBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    //    private List<CardNewsEntity> cardNewsList;
    private static final int VIEW_PROG = 0;
    private static final int VIEW_ITEM = 1;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

}
