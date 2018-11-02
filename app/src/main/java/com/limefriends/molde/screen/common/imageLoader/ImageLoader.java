package com.limefriends.molde.screen.common.imageLoader;

import android.content.Context;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

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

    public void load(String url, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .into(imageView);
    }

    public void load(String url, SimpleTarget<GlideDrawable> target) {
        Glide.with(mContext)
                .load(url)
                .into(target);
    }

    public void load(String url, int placeHolder, ImageView imageView) {
        Glide.with(mContext)
                .load(url)
                .placeholder(placeHolder)
                .into(imageView);
    }

}
