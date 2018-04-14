package com.limefriends.molde.menu_magazine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.limefriends.molde.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MagazineCardnewsDetailActivity extends AppCompatActivity {
    @BindView(R.id.cardnews_viewPager)
    ViewPager cardnews_viewPager;
    @BindView(R.id.img_comment)
    ImageView img_comment;

    CustomAdapter adapter;
    ArrayList list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_cardnews_detail);
        ButterKnife.bind(this);

        list = new ArrayList();
        for (int i = 0; i < 3; i++) {
            list.add(R.drawable.letters1 + i);
        }

        // CustomAdapter에 LayoutInflater 객체 전달하고,
        // ViewPager에 Adapter 설정
        adapter = new CustomAdapter(getLayoutInflater(), list);
        cardnews_viewPager.setAdapter(adapter);
        cardnews_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("TAG", String.valueOf(position));
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("TAG", String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("TAG", String.valueOf(state));
            }
        });

        img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
