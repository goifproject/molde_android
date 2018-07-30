package com.limefriends.molde.menu_mypage.report;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.pm10.library.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPageMyReportDetailActivity extends AppCompatActivity {
    List<String> reportImageLinkList;

    @BindView(R.id.mypage_detail_report_image_indicator)
    CircleIndicator mypage_detail_report_image_indicator;
    @BindView(R.id.mypage_detail_report_image_pager)
    ViewPager mypage_detail_report_image_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_my_report_detail);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("신고 상세 내역");

        reportImageLinkList = new ArrayList<String>();
        reportImageLinkList.add("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Hong_Kong_Night_view.jpg/450px-Hong_Kong_Night_view.jpg");
        reportImageLinkList.add("http://static.hubzum.zumst.com/hubzum/2015/11/05/10/34819c80b4834b678f1dc127242c42cd.jpg");
        mypage_detail_report_image_pager.setAdapter(
                new MyPageMyReportImageAdapter(getApplicationContext(),
                (ArrayList<String>) reportImageLinkList)
        );
        mypage_detail_report_image_indicator.setupWithViewPager(mypage_detail_report_image_pager);
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
