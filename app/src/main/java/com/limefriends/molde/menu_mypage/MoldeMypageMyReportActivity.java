package com.limefriends.molde.menu_mypage;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMypageMyReportActivity extends AppCompatActivity {

    @BindView(R.id.myReport_recyclerView)
    RecyclerView myReport_recyclerView;

    MyPageMyReportAdapter adapter;
    List<MyPageMyReportElem> myReportElemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molde_mypage_my_report);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("내 신고 내역");


        myReportElemList = new ArrayList<MyPageMyReportElem>();

        myReportElemList.add(new MyPageMyReportElem(R.drawable.report_map_img,
                "2018. 03. 01", "서울시 마포구 와우산로 92 체육관 2층 여자화장실 1번째 칸"));

        myReportElemList.add(new MyPageMyReportElem(R.drawable.report_map_img,
                "2018. 03. 02", "서울시 마포구 와우산로 92 체육관 2층 여자화장실 2번째 칸"));

        myReportElemList.add(new MyPageMyReportElem(R.drawable.report_map_img,
                "2018. 03. 03", "서울시 마포구 와우산로 92 체육관 2층 여자화장실 3번째 칸"));

        myReportElemList.add(new MyPageMyReportElem(R.drawable.report_map_img,
                "2018. 03. 04", "서울시 마포구 와우산로 92 체육관 2층 여자화장실 4번째 칸"));


        adapter = new MyPageMyReportAdapter(getApplicationContext(),
                R.layout.activity_molde_mypage_my_report, myReportElemList);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        myReport_recyclerView.setLayoutManager(layoutManager);
        myReport_recyclerView.setAdapter(adapter);

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