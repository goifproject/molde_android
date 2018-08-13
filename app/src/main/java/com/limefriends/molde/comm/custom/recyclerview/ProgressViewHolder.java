package com.limefriends.molde.comm.custom.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.limefriends.molde.R;

public class ProgressViewHolder extends RecyclerView.ViewHolder {

    ProgressBar feedLoadingBar;

    public ProgressViewHolder(View v) {
        super(v);
        feedLoadingBar = v.findViewById(R.id.feed_loading_bar);
    }

}
