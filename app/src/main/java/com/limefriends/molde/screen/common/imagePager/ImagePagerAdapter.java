package com.limefriends.molde.screen.common.imagePager;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.model.entity.ImageData;
import com.limefriends.molde.model.entity.cardNews.CardNewsImageEntity;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.common.imagePager.itemView.ImagePagerView;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter<T extends ImageData> extends PagerAdapter {

    private List<T> newsImg = new ArrayList<>();
    private ViewFactory mViewFactory;

    public ImagePagerAdapter(ViewFactory viewFactory) {
        this.mViewFactory = viewFactory;
    }

    public void setData(List<T> newsImg) {
        this.newsImg = newsImg;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return newsImg.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImagePagerView view = mViewFactory.newInstance(ImagePagerView.class, container);
        view.bindImage(newsImg.get(position).getImageUrl());
        View rootView = view.getRootView();
        container.addView(rootView);
        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


}
