package com.limefriends.molde.screen.magazine.detail;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.cardNews.CardNewsImageEntity;
import com.limefriends.molde.networking.schema.cardNews.CardNewsImageSchema;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.magazine.detail.view.CardNewsDetailPagerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardNewsDetailPagerAdapter extends PagerAdapter {

    private List<CardNewsImageEntity> newsImg = new ArrayList<>();
    private ViewFactory mViewFactory;

    public CardNewsDetailPagerAdapter(ViewFactory viewFactory) {
        this.mViewFactory = viewFactory;
    }

    public void setData(List<CardNewsImageEntity> newsImg) {
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

        CardNewsDetailPagerView view
                = mViewFactory.newInstance(CardNewsDetailPagerView.class, container);
        view.bindCardNewsImage(newsImg.get(position).getUrl());
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
