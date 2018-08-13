package com.limefriends.molde.ui.menu_magazine.report;


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

public class MagazineReportMolcaDetailActivity extends AppCompatActivity {
    @BindView(R.id.btn_molca_report_call)
    FloatingActionButton btn_report_call;

    @BindView(R.id.txt_new_molca_01)
    TextView txt_new_molca_01;
    @BindView(R.id.txt_new_molca_02)
    TextView txt_new_molca_02;
    @BindView(R.id.txt_new_molca_03)
    TextView txt_new_molca_03;
    @BindView(R.id.txt_new_molca_04)
    TextView txt_new_molca_04;
    @BindView(R.id.txt_new_molca_05)
    TextView txt_new_molca_05;
    @BindView(R.id.txt_new_molca_06)
    TextView txt_new_molca_06;
    @BindView(R.id.txt_new_molca_07)
    TextView txt_new_molca_07;

    @BindView(R.id.img_new_molca_01)
    ImageView img_new_molca_01;
    @BindView(R.id.img_new_molca_02)
    ImageView img_new_molca_02;
    @BindView(R.id.img_new_molca_03)
    ImageView img_new_molca_03;
    @BindView(R.id.img_new_molca_04)
    ImageView img_new_molca_04;
    @BindView(R.id.img_new_molca_05)
    ImageView img_new_molca_05;
    @BindView(R.id.img_new_molca_06)
    ImageView img_new_molca_06;
    @BindView(R.id.img_new_molca_07)
    ImageView img_new_molca_07;
    @BindView(R.id.img_new_molca_08)
    ImageView img_new_molca_08;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazine_activity_molca_report_detail);
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
        String txt01 = "어떠한 몰래카메라가 있는지 알면 아무래도 더 조심할 수 있겠죠?\n\n" +
                "다양한 형태의 몰카와 현재 유행하는 몰카형태를 알아봅시다.";
        String txt02 = "1. 시계 몰카\n\n" +
                "일상생활에서 많이 착용하는 시계도 몰카로 사용되곤 합니다. " +
                "손목 시계의 경우 분침과 초침이 있는 시계 이외에 전자 손목시계 역시 몰카 형태가 존재한다는 것! " +
                "손목시계의 시간을 나타내는 동그란 원 안쪽에 카메라가 있는 경우도 있고, " +
                "동그란 원 옆면에 카메라가 있는 경우도 있으니 한 부분만 보고 안심할 수는 없어요. " +
                "또 손목 시계가 아닌 탁상용 시계 형태의 몰카도 있으니 조심하세요!";
        String txt03 = "2. 안경 몰카\n\n" +
                "흔하게 볼 수 있는 안경 안에도 몰카가 숨어있을 수 있습니다. " +
                "기본적인 뿔테 안경과 비슷해 보이지만 중앙에 렌즈가 있다는 차이가 있습니다. " +
                "걸그룹 ‘여자친구’ 역시 팬미팅 때 안경 몰카를 쓰고 온 사람을 발견해서 이슈가 되었었죠. " +
                "최근에는 렌즈가 눈에 띄게 보이지 않는 형태로 존재하기도 한다는데요. " +
                "계속 지나치게 쳐다보는 타인이 이런 안경을 쓰고 있다면 조심하세요!";
        String txt04 = "3. 볼펜 몰카\n\n" +
                "고급스럽고 비싸 보이는 이 펜은 실제로도 싸지 않은 30만원 정도의 몰래 카메라입니다. " +
                "또 펜 형태의 몰카의 경우, 펜의 형태를 활용해 렌즈캡을 열고 닫는 것이 가능하다고 하는데요. " +
                "렌즈 캡이 닫혀 있으면 아무리 자세히 봐도 카메라를 발견할 수가 없겠죠. " +
                "혹시 이렇게 좋아 보이는 펜이 여기 있을 필요가 없는데 있다! 라고 하는 경우, " +
                "펜을 요리조리 움직여보며 렌즈캡 또는 렌즈를 찾아보는 것도 한가지 몰카 발견 방법이 될 수 있겠죠.";
        String txt05 = "4. 차키 몰카\n\n" +
                "흔하게 볼 수 있는 차 키의 모양을 하고 있는 몰래 카메라도 있답니다. " +
                "차 키를 손에 쥐고 있을 필요가 없는데 쥐고 어디를 지속적으로 향하고 있다거나, " +
                "혹은 의외의 장소에 차 키가 인테리어처럼 놓여져 있다면 한번 살펴보면 어떨까요?";
        String txt06 = "5. 기타 휴대용 몰카\n\n" +
                "너무나 평범해 보이는 모자, 넥타이 역시 몰래 카메라가 숨어 있는, 시중에 판매되고 있는 몰카일 수 있습니다. " +
                "이 외에도 물통, 인형, 심지어는 휴대전화 케이스 까지 위장형 몰래카메라로 판매되고 있다는 사실! " +
                "너무나 평범한 물건들이라 몰래 카메라를 의심하기 어렵고 발견하기도 어렵겠지만, " +
                "이렇게 한번이라도 정보를 봐 놓으면 조금은 더 쉽게 몰카를 발견할 수 있지 않을까요?";
        String txt07 = "5. 인테리어 몰카\n\n" +
                "어느 장소에서나 쉽게 발견할 수 있는 마우스, 옷걸이, 액자, 벽 스위치, 화재경보기 등도 실제 몰래 카메라일 수 있습니다. " +
                "모든 그림 액자마다 한 곳 한 곳 자세히 볼 수도 없겠지만 " +
                "이런 그림들이 몰카로 팔리고 있구나 정도라도 참고해 둔다면 조금 더 도움이 될 듯해요!\n" +
                "숙소에서 불안하시다면 꼭 한번 체크해보세요.";

        txt_new_molca_01.setText(txt01);
        txt_new_molca_02.setText(txt02);
        txt_new_molca_03.setText(txt03);
        txt_new_molca_04.setText(txt04);
        txt_new_molca_05.setText(txt05);
        txt_new_molca_06.setText(txt06);
        txt_new_molca_07.setText(txt07);

        Glide.with(this).load(R.drawable.img_01_01).into(img_new_molca_01);
        Glide.with(this).load(R.drawable.img_01_02).into(img_new_molca_02);
        Glide.with(this).load(R.drawable.img_01_03).into(img_new_molca_03);
        Glide.with(this).load(R.drawable.img_01_04).into(img_new_molca_04);
        Glide.with(this).load(R.drawable.img_01_05).into(img_new_molca_05);
        Glide.with(this).load(R.drawable.img_01_06).into(img_new_molca_06);
        Glide.with(this).load(R.drawable.img_01_07).into(img_new_molca_07);
        Glide.with(this).load(R.drawable.img_01_08).into(img_new_molca_08);
        /* put contents */

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
