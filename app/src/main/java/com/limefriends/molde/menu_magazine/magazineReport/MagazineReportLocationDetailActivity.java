package com.limefriends.molde.menu_magazine.magazineReport;


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

public class MagazineReportLocationDetailActivity extends AppCompatActivity {
    @BindView(R.id.btn_location_report_call)
    FloatingActionButton btn_report_call;

    @BindView(R.id.txt_molca_by_location_01)
    TextView txt_molca_by_location_01;
    @BindView(R.id.txt_molca_by_location_02)
    TextView txt_molca_by_location_02;
    @BindView(R.id.txt_molca_by_location_03)
    TextView txt_molca_by_location_03;
    @BindView(R.id.txt_molca_by_location_04)
    TextView txt_molca_by_location_04;
    @BindView(R.id.txt_molca_by_location_05)
    TextView txt_molca_by_location_05;

    @BindView(R.id.img_molca_by_location_01)
    ImageView img_molca_by_location_01;
    @BindView(R.id.img_molca_by_location_02)
    ImageView img_molca_by_location_02;
    @BindView(R.id.img_molca_by_location_03)
    ImageView img_molca_by_location_03;
    @BindView(R.id.img_molca_by_location_04)
    ImageView img_molca_by_location_04;
    @BindView(R.id.img_molca_by_location_05)
    ImageView img_molca_by_location_05;
    @BindView(R.id.img_molca_by_location_06)
    ImageView img_molca_by_location_06;
    @BindView(R.id.img_molca_by_location_07)
    ImageView img_molca_by_location_07;
    @BindView(R.id.img_molca_by_location_08)
    ImageView img_molca_by_location_08;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_location_report_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(title);

        btn_report_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                startActivity(intent);
            }
        });

        /* put contents */
        String txt01 = "1. 화장실 몰카 예방법\n\n" +
                "-   화장실 이용 시 위와 아래를 잘 살피고, 휴지통에 신문지가 덮여 있을 경우 확인해야 합니다. \n" +
                "-   남녀 공용 화장실인 경우 더욱 주의가 요구됩니다.\n" +
                "-   1층 화장실에 창문이 있다면 잘 살펴봐야 합니다.\n" +
                "-   환풍구에서도 몰래카메라가 발견되곤 합니다.";
        String txt02 = "-   천장 합판을 잇는 텍스 중 튀는 나사구멍이 있다면 카메라일 가능성이 있습니다.\n" +
                "-   화장실 문쪽 나사 역시 주의해야 합니다.\n" +
                "-   공중화장실 등을 이용하면서 의심스러운 위치에 있는 나사, 혹은 구멍이 보이면 112에 신고합니다. ";
        String txt03 = "2. 지하철 몰카 예방법\n\n" +
                "-   공간이 있음에도 누군가 바짝 붙는다면 거리를 유지해야 합니다.\n" +
                "-   손, 발, 가방, 우산 등이 본인 무릎 아래로 향할 경우 카메라가 있을 수 있습니다.";
        String txt04 = "- 지하철에서 계단을 오를 때는 뒤를 핸드백이나 책 등으로 가리기\n" +
                "에스컬레이터는 45도 각도로 가장자리에 위치하고 약간 비스듬히 서서 밑을 바라보며 올라갑니다. \n\n" +
                "- 몰카에 찍힌 것으로 의심되면 관련자가 영상이나 사진을 지우기 전 신속히 경찰에 신고하는 것이 좋고, 인상착의를 기억해두면 수사에 도움이 됩니다. ";
        String txt05 = "3. 숙소 몰카 예방법\n\n" +
                "- 인테리어와 어울리지 않는 가구가 있다면 의심해야 합니다.\n" +
                "- TV 리모컨 수신 센서, 시계바늘 중앙이나 다른 곳의 구멍은 카메라를 숨기기 좋습니다.\n" +
                "- 휴지곽, 옷걸이 등 구멍이 있거나 뭔가 반짝인다면 자세히 보세요.";

        txt_molca_by_location_01.setText(txt01);
        txt_molca_by_location_02.setText(txt02);
        txt_molca_by_location_03.setText(txt03);
        txt_molca_by_location_04.setText(txt04);
        txt_molca_by_location_05.setText(txt05);


        Glide.with(this).load(R.drawable.img_02_01).into(img_molca_by_location_01);
        Glide.with(this).load(R.drawable.img_02_02).into(img_molca_by_location_02);
        Glide.with(this).load(R.drawable.img_02_03).into(img_molca_by_location_03);
        Glide.with(this).load(R.drawable.img_02_04).into(img_molca_by_location_04);
        Glide.with(this).load(R.drawable.img_02_05).into(img_molca_by_location_05);
        Glide.with(this).load(R.drawable.img_02_06).into(img_molca_by_location_06);
        Glide.with(this).load(R.drawable.img_02_07).into(img_molca_by_location_07);
        Glide.with(this).load(R.drawable.img_02_08).into(img_molca_by_location_08);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
