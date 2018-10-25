package com.limefriends.molde.screen.magazine.detail;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.networking.schema.news.CardNewsImageSchema;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardNewsImagePagerAdapter extends PagerAdapter {

    @BindView(R.id.cardnews_image)
    ImageView cardnews_image;

    private LayoutInflater inflater;
    private List<CardNewsImageSchema> newsImg = new ArrayList<>();

    public void setData(List<CardNewsImageSchema> newsImg) {
        this.newsImg = newsImg;
        notifyDataSetChanged();
    }

    CardNewsImagePagerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return newsImg.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = inflater.inflate(R.layout.item_cardnews_image,
                container, false);
        ButterKnife.bind(this, view);
        Glide.with(view.getContext()).load(newsImg.get(position).getUrl())
                .placeholder(R.drawable.img_placeholder_magazine).into(cardnews_image);
        container.addView(view);
        return view;
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
