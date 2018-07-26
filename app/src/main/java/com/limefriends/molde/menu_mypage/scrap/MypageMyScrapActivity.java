package com.limefriends.molde.menu_mypage.scrap;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_mypage.entity.MyPageMyScrapEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MypageMyScrapActivity extends AppCompatActivity {

    @BindView(R.id.myScrap_recyclerView)
    RecyclerView myScrap_recyclerView;

    MyPageMyScrapAdapter adapter;
    List<MyPageMyScrapEntity> myPageMyScrapEntityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molde_mypage_my_scrap);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("내 스크랩");


        myPageMyScrapEntityList = new ArrayList<MyPageMyScrapEntity>();

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "에어비앤*에서 다시 몰카 발각"));

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "몰카발각 법적 처벌 강화…"));

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "최신 몰카트렌드"));

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "몰카범 지하철역에서 잡혀…"));

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "제목제목"));

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "제-목"));

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "제목!!"));

        myPageMyScrapEntityList.add(new MyPageMyScrapEntity(R.drawable.mypage_image,
                "제목??"));


        adapter = new MyPageMyScrapAdapter(getApplicationContext(),
                R.layout.activity_molde_mypage_my_scrap, myPageMyScrapEntityList);


        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        myScrap_recyclerView.setLayoutManager(layoutManager);
        myScrap_recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}