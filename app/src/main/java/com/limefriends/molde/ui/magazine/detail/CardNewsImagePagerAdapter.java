package com.limefriends.molde.ui.magazine.detail;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.news.CardNewsImageResponseInfoEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO 뷰 리스트 만들어서 재활용 할 것
// TODO 핀치 줌으로 사진 확대해서 볼 수 있도록 할 것
public class CardNewsImagePagerAdapter extends PagerAdapter {

    @BindView(R.id.cardnews_image)
    ImageView cardnews_image;

    private LayoutInflater inflater;
    private List<CardNewsImageResponseInfoEntity> newsImg = new ArrayList<>();

    public void setData(List<CardNewsImageResponseInfoEntity> newsImg) {
        this.newsImg = newsImg;
        notifyDataSetChanged();
    }

    public CardNewsImagePagerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return newsImg.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = inflater.inflate(R.layout.magazine_cardnews_detail_item,
                container, false);
        ButterKnife.bind(this, view);
        Glide.with(view.getContext()).load(newsImg.get(position).getUrl())
                .placeholder(R.drawable.img_cardnews_dummy).into(cardnews_image);
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
