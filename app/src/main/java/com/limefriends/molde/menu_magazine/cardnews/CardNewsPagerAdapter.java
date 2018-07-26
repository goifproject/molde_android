package com.limefriends.molde.menu_magazine.cardnews;

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
import com.limefriends.molde.menu_magazine.entity.CardnewsDetailEntity;

import java.util.ArrayList;

/**
 * Created by joo on 2018. 4. 10..
 */

public class CardNewsPagerAdapter extends PagerAdapter {


    ImageView img_viewPager_child;
    ScrollView cardnews_description_wrapper;
    TextView txt_cardnews_description;
    LayoutInflater inflater;
    ArrayList list;
    CardnewsDetailEntity element;


    public CardNewsPagerAdapter(LayoutInflater inflater, ArrayList list) {
        this.inflater = inflater;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = inflater.inflate(R.layout.magazine_cardnews_viewpager_child, null);
        img_viewPager_child = (ImageView)view.findViewById(R.id.img_cardnews_viewPager_child);
        cardnews_description_wrapper = (ScrollView)view.findViewById(R.id.cardnews_description_wrapper);
        txt_cardnews_description = (TextView)view.findViewById(R.id.txt_cardnews_description);

        element = (CardnewsDetailEntity)list.get(position);
        int image = element.getImage();
        Toast.makeText(view.getContext(), String.valueOf(image), Toast.LENGTH_SHORT).show();
        String description = element.getDescription();

        Glide.with(view.getContext()).load(image).into(img_viewPager_child);
        txt_cardnews_description.setText(description);
        container.addView(view);
        return view;

        // TODO textview margin

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
