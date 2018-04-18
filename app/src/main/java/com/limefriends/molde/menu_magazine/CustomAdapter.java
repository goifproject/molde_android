package com.limefriends.molde.menu_magazine;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by joo on 2018. 4. 10..
 */

public class CustomAdapter extends PagerAdapter {
    ImageView img_viewPager_child;
    LayoutInflater inflater;
    ArrayList list;


    public CustomAdapter(LayoutInflater inflater, ArrayList list) {
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

        int image = (int)list.get(position);
        Intent intent = new Intent();
        intent.putExtra("postion", position);


        Glide.with(view.getContext()).load(image).into(img_viewPager_child);
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
