package com.limefriends.molde.menu_magazine.cardnews;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_magazine.entity.CardNewsDetailEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineCardNewsDetailPagerAdapter extends PagerAdapter {
    @BindView(R.id.cardnews_image)
    ImageView cardnews_image;
    @BindView(R.id.cardnews_description_wrapper)
    ScrollView cardnews_description_wrapper;
    @BindView(R.id.cardnews_description)
    TextView cardnews_description;

    private LayoutInflater inflater;
    private ArrayList<CardNewsDetailEntity> cardNewsDetailList;

    public MagazineCardNewsDetailPagerAdapter(LayoutInflater inflater, ArrayList<CardNewsDetailEntity> cardNewsDetailList) {
        this.inflater = inflater;
        this.cardNewsDetailList = cardNewsDetailList;
    }

    @Override
    public int getCount() {
        return cardNewsDetailList.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.magazine_cardnews_detail_item, null);
        ButterKnife.bind(this, view);
        CardNewsDetailEntity cardNewsDetailEntity = cardNewsDetailList.get(position);
        int image = cardNewsDetailEntity.getImage();
        Toast.makeText(view.getContext(), String.valueOf(image), Toast.LENGTH_SHORT).show();
        String description = cardNewsDetailEntity.getDescription();
        Glide.with(view.getContext()).load(image).into(cardnews_image);
        cardnews_description.setText(description);
        container.addView(view);
        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
