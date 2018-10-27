package com.limefriends.molde.screen.common.imageLoader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader {

    private Context mContext;

    public ImageLoader(Context mContext) {
        this.mContext = mContext;
    }

    public void load(int url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .into(imageView);
    }

    public void load(String url, int placeHolder, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .placeholder(placeHolder)
                .into(imageView);
    }


}
