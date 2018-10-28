package com.limefriends.molde.screen.magazine.detail.view.viewImpl;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.views.BaseView;
import com.limefriends.molde.screen.magazine.detail.view.CardNewsDetailPagerView;

public class CardNewsDetailPagerViewImpl extends BaseView implements CardNewsDetailPagerView {

    private ImageView cardnews_image;

    private final ImageLoader mImageLoader;

    public CardNewsDetailPagerViewImpl(LayoutInflater inflater,
                                       ViewGroup parent,
                                       ImageLoader imageLoader) {

        setRootView(inflater.inflate(R.layout.item_cardnews_image, parent, false));

        this.mImageLoader = imageLoader;

        cardnews_image = findViewById(R.id.cardnews_image);
    }


    @Override
    public void bindCardNewsImage(String url) {
        mImageLoader.load(url, R.drawable.img_placeholder_magazine, cardnews_image);
    }
}
