package com.limefriends.molde.ui.menu_magazine.detail;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.news.MoldeCardNewsEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsImageResponseInfoEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineCardNewsDetailPagerAdapter extends PagerAdapter {
    @BindView(R.id.cardnews_image)
    ImageView cardnews_image;
//    @BindView(R.id.cardnews_description_wrapper)
//    ScrollView cardnews_description_wrapper;
//    @BindView(R.id.cardnews_description)
//    TextView cardnews_description;

    private View view;
    private LayoutInflater inflater;
    private List<MoldeCardNewsImageResponseInfoEntity> newsImg = new ArrayList<>();

    public void setData(List<MoldeCardNewsImageResponseInfoEntity> newsImg) {
        this.newsImg = newsImg;
        notifyDataSetChanged();
    }

    public MagazineCardNewsDetailPagerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return newsImg.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        if (view == null) {
        // TODO 뷰 리스트 만들어서 재활용 할 것
        // TODO 핀치 줌으로 사진 확대해서 볼 수 있도록 할 것
            view = inflater.inflate(R.layout.magazine_cardnews_detail_item, container, false);
            ButterKnife.bind(this, view);
//        }
        // MoldeCardNewsEntity cardNewsDetailEntity = cardNewsDetailList.get(position);
        // int image = cardNewsDetailEntity.getImage();
        // Toast.makeText(view.getContext(), String.valueOf(image), Toast.LENGTH_SHORT).show();
        // String description = cardNewsDetailEntity.getDescription();

        Glide.with(view.getContext()).load(newsImg.get(position).getUrl()).into(cardnews_image);

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
