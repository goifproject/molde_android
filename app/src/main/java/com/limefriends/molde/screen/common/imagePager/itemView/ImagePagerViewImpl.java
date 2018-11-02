package com.limefriends.molde.screen.common.imagePager.itemView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.views.BaseView;

public class ImagePagerViewImpl extends BaseView implements ImagePagerView {

    private ImageView cardnews_image;

    private final ImageLoader mImageLoader;

    public ImagePagerViewImpl(LayoutInflater inflater,
                              ViewGroup parent,
                              ImageLoader imageLoader) {

        setRootView(inflater.inflate(R.layout.item_cardnews_image, parent, false));

        this.mImageLoader = imageLoader;

        cardnews_image = findViewById(R.id.cardnews_image);
    }


    @Override
    public void bindImage(String url) {
        mImageLoader.load(url, R.drawable.img_placeholder_magazine, cardnews_image);
    }
}
