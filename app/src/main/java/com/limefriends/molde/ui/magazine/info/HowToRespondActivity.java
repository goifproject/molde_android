package com.limefriends.molde.ui.magazine.info;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HowToRespondActivity extends AppCompatActivity {

    @BindView(R.id.btn_spread_report_call)
    FloatingActionButton btn_report_call;

    @BindView(R.id.txt_molca_spreading_title_01)
    TextView txt_molca_spreading_title_01;
    @BindView(R.id.txt_molca_spreading_title_02)
    TextView txt_molca_spreading_title_02;
    @BindView(R.id.txt_molca_spreading_title_03)
    TextView txt_molca_spreading_title_03;
    @BindView(R.id.txt_molca_spreading_title_04)
    TextView txt_molca_spreading_title_04;

    @BindView(R.id.txt_molca_spreading_01)
    TextView txt_molca_spreading_01;
    @BindView(R.id.txt_molca_spreading_02)
    TextView txt_molca_spreading_02;
    @BindView(R.id.txt_molca_spreading_03)
    TextView txt_molca_spreading_03;
    @BindView(R.id.txt_molca_spreading_04)
    TextView txt_molca_spreading_04;
    @BindView(R.id.txt_molca_spreading_05)
    TextView txt_molca_spreading_05;

    @BindView(R.id.img_molca_spreading_01)
    ImageView img_molca_spreading_01;
    @BindView(R.id.img_molca_spreading_02)
    ImageView img_molca_spreading_02;
    @BindView(R.id.img_molca_spreading_03)
    ImageView img_molca_spreading_03;
    @BindView(R.id.img_molca_spreading_04)
    ImageView img_molca_spreading_04;
    @BindView(R.id.img_molca_spreading_05)
    ImageView img_molca_spreading_05;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_respond);

        setupViews();

        setupListeners();

        setupHowToRespondInfo();
    }

    private void setupViews() {
        ButterKnife.bind(this);
        String title = getIntent().getStringExtra("title");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);
    }

    private void setupListeners() {
        btn_report_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                startActivity(intent);
            }
        });
    }

    private void setupHowToRespondInfo() {
        String txt01 = "디지털 성범죄 피해는 속도가 생명이다. 증거 수집도, 신고도 서둘러야 한다. " +
                "가해자 신고와 영상 삭제, 심리 상담 등 모든 과정에 시민단체와 정부의 도움을 받을 수 있다.";

        String title01 = "1. 관할 경찰서에 신고하기";
        String txt02 = "빠른 수사를 위해선: 발견한 영상을 담은 usb, " +
                "영상에 나온 사람이 본인이라는 것을 증명할 수 있는 얼굴과 신체 특징이 담긴 캡처 사진, " +
                "유포 영상 리스트 등의 증거물을 준비해 관할 경찰서에 제출하는 것이 좋다. " +
                "증거자료 없이도 신고할 수 있지만 자료를 제시하면 수사가 빠르게 진척될 수 있다. " +
                "또한 피해자는 여성 수사관 및 독립된 진술 공간도 요청할 수 있다. \n\n" +
                "혼자 경찰 신고를 준비하는 일이 힘들다면 \n" +
                "‘한국사이버성폭력대응센터’: http://cyber-lion.com\n" +
                "‘디지털성범죄아웃(DSO)’: http://dsoonline.org\n" +
                "등의 도움을 받을 수 있다. 이 단체들은 경위서를 대신 작성해주거나, 증거 수집, 경찰서 신고 접수 등을 지원한다. ";

        String title02 = "2. 영상 유포로 인한 피해 줄이기";
        String txt03 = "-   직접 ‘방송통신심의위원회’에 삭제 요청하기: \n" +
                "방송통신심의위원회: http://www.kocsc.or.kr/mainPage.do\n" +
                "사이트의 ‘인터넷피해구제센터-권리침해정보심의’에 올리면 된다. \n\n" +
                "-   전화신고: 국번 없이 1377 \n\n" +
                "-   직접 주요 포털이나 사회관계망서비스 업체에 삭제 요청을 보내기: \n\n" +
                "-   직접 피해 영상을 찾아내는게 힘들다면?:\n" +
                "한국사이버성폭력대응센터 http://cyber-lion.com \n" +
                "디지털성범죄아웃(DSO) http://dsoonline.org\n" +
                "의 도움을 받을 수 있다. ‘잊힐 권리를 위임한다’는 위임장과 신분증 복사본을 센터에 전달하면 된다. ";

        String title03 = "3. 법률상담 및 심리상담 지원";
        String txt04 = "-   여성가족부의 무료법률지원 서비스를 받을 수 있다. " +
                "전국의 성폭력상담소나 해바라기센터(대표전화 1899-3075)로 연락하면 된다. \n" +
                "-   사이버 성폭력 피해에 대한 심리 상담이 필요할 경우 여성가족부에서 운영하는 여성긴급전화(대표번호 1366)를 이용하거나, " +
                "게시판·카카오톡·채팅을 통해 전문 상담원과 사이버상담(www.women1366.kr)을 할 수 있다. \n" +
                "(출처 : http://www.hani.co.kr/arti/society/society_general/809840)";

        String title04 = "4.  여성가족부 – ‘디지털 성범죄 피해자 지원센터’";
        String txt05 = "디지털 성범죄 피해에 대한 상담, 삭제지원, 수사지원, 소송지원, 사후모니터링 등 종합적인 서비스를 " +
                "원스톱으로 지원 (2018년 4월 30일부터 운영) " +
                "상담 신청은 온라인 게시판 및 전화로 접수 가능하다.\n\n" +
                "상담 신청 방법: \n" +
                "온라인 비공개 게시판: www.women1366.kr/stopds \n" +
                "전화: 02-735-8994 (평일 10:00~17:00)";

        txt_molca_spreading_01.setText(txt01);
        txt_molca_spreading_02.setText(txt02);
        txt_molca_spreading_03.setText(txt03);
        txt_molca_spreading_04.setText(txt04);
        txt_molca_spreading_05.setText(txt05);

        txt_molca_spreading_title_01.setText(title01);
        txt_molca_spreading_title_02.setText(title02);
        txt_molca_spreading_title_03.setText(title03);
        txt_molca_spreading_title_04.setText(title04);


        Glide.with(this).load(R.drawable.img_respond_info_01).into(img_molca_spreading_01);
        Glide.with(this).load(R.drawable.img_respond_info_02).into(img_molca_spreading_02);
        Glide.with(this).load(R.drawable.img_respond_info_03).into(img_molca_spreading_03);
        Glide.with(this).load(R.drawable.img_respond_info_04).into(img_molca_spreading_04);
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
