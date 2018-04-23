package com.limefriends.molde.menu_magazine;

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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/*** make a pair with magazine_activity_cardnews_detail.xml ***/


public class MagazineCardnewsDetailActivity extends AppCompatActivity {

    @BindView(R.id.cardnews_fragment_container)
    RelativeLayout cardnews_fragment_container;

    @BindView(R.id.cardnews_viewPager)
    ViewPager cardnews_viewPager;

    @BindView(R.id.img_comment)
    ImageView img_comment;

    @BindView(R.id.txt_current_pagenum)
    TextView txt_current_pagenum;

    @BindView(R.id.txt_total_pagenum)
    TextView txt_total_pagenum;


    CustomAdapter adapter;
    ArrayList list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_cardnews_detail);
        ButterKnife.bind(this);


        list = new ArrayList<CardnewsDetailListElement>();

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
            list.add(new CardnewsDetailListElement(R.drawable.letters1 + i, description));
        }

        txt_total_pagenum.setText(String.valueOf(list.size()));



        // CustomAdapter에 LayoutInflater 객체 전달하고,
        // ViewPager에 Adapter 설정
        adapter = new CustomAdapter(getLayoutInflater(), list);
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
                intent.setClass(getApplicationContext(), MagazineCommentActivity.class);
                intent.putExtra("toolbarTitle", "댓글");
                startActivity(intent);
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
