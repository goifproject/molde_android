package com.limefriends.molde.menu_magazine.cardnews;

/**
 * Created by joo on 2018. 4. 9..
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_magazine.comment.CommentActivity;
import com.limefriends.molde.menu_magazine.entity.CardnewsDetailEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CardnewsDetailActivity extends AppCompatActivity {

    @BindView(R.id.cardnews_fragment_container)
    RelativeLayout cardnews_fragment_container;

    @BindView(R.id.cardnews_viewPager)
    ViewPager cardnews_viewPager;

    @BindView(R.id.img_comment)
    ImageView img_comment;

    @BindView(R.id.img_cardnews_scrap)
    ImageView img_cardnews_scrap;

    @BindView(R.id.img_cardnews_share)
    ImageView img_cardnews_share;

    @BindView(R.id.txt_current_pagenum)
    TextView txt_current_pagenum;

    @BindView(R.id.txt_total_pagenum)
    TextView txt_total_pagenum;


    CardNewsPagerAdapter adapter;
    ArrayList list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_cardnews_detail);
        ButterKnife.bind(this);


        list = new ArrayList<CardnewsDetailEntity>();

        /* put dummy data */
        for (int i = 0; i < 3; i++) {
            String description = String.valueOf(i + 1) + "번째 페이지\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n"
                    + "How Can I Add Many TextView or Other Views in ViewPager\n";
            list.add(new CardnewsDetailEntity(R.drawable.letters1 + i, description));
        }

        txt_total_pagenum.setText(String.valueOf(list.size()));



        // CustomAdapter에 LayoutInflater 객체 전달하고,
        // ViewPager에 Adapter 설정
        adapter = new CardNewsPagerAdapter(getLayoutInflater(), list);
        cardnews_viewPager.setAdapter(adapter);
        cardnews_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                txt_current_pagenum.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



        img_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "comment clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), CommentActivity.class);
                intent.putExtra("toolbarTitle", "댓글");
                startActivity(intent);

            }
        });

        img_cardnews_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_cardnews_scrap.setImageResource(R.drawable.ic_cardscrap_2_24_true);
                // TODO 스크랩 추가 <-> 취소 toggle
                Toast.makeText(getApplicationContext(), "스크랩 추가", Toast.LENGTH_SHORT).show();
            }
        });

        img_cardnews_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "공유하기", Toast.LENGTH_SHORT).show();
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
