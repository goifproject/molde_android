package com.limefriends.molde.menu_magazine.cardnews;

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
import com.limefriends.molde.menu_magazine.comment.MagazineCommentActivity;
import com.limefriends.molde.menu_magazine.entity.CardNewsDetailEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MagazineCardnewsDetailActivity extends AppCompatActivity {
    @BindView(R.id.cardnews_detail_layout)
    RelativeLayout cardnews_detail_layout;
    @BindView(R.id.cardnews_pager)
    ViewPager cardnews_pager;
    @BindView(R.id.cardnews_comment)
    ImageView cardnews_comment;
    @BindView(R.id.cardnews_scrap)
    ImageView cardnews_scrap;
    @BindView(R.id.cardnews_share)
    ImageView cardnews_share;
    @BindView(R.id.current_page_no)
    TextView current_page_no;
    @BindView(R.id.total_page_no)
    TextView total_page_no;

    MagazineCardNewsDetailPagerAdapter magazineCardNewsDetailPagerAdapter;
    List<CardNewsDetailEntity> cardNewsDetailList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_cardnews_detail);
        ButterKnife.bind(this);

        cardNewsDetailList = new ArrayList<CardNewsDetailEntity>();

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
            cardNewsDetailList.add(new CardNewsDetailEntity(R.drawable.letters1 + i, description));
        }

        total_page_no.setText(String.valueOf(cardNewsDetailList.size()));

        magazineCardNewsDetailPagerAdapter = new MagazineCardNewsDetailPagerAdapter(getLayoutInflater(), (ArrayList<CardNewsDetailEntity>) cardNewsDetailList);
        cardnews_pager.setAdapter(magazineCardNewsDetailPagerAdapter);
        cardnews_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                current_page_no.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });



        cardnews_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "comment clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MagazineCommentActivity.class);
                intent.putExtra("toolbarTitle", "댓글");
                startActivity(intent);

            }
        });

        cardnews_scrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardnews_scrap.setImageResource(R.drawable.ic_card_scrap_true);
                Toast.makeText(getApplicationContext(), "스크랩 추가", Toast.LENGTH_SHORT).show();
            }
        });

        cardnews_share.setOnClickListener(new View.OnClickListener() {
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
