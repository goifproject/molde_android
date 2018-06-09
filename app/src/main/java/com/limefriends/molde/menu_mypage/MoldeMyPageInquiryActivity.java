package com.limefriends.molde.menu_mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_mypage.backend_data_type.MoldeFaQ;
import com.limefriends.molde.molde_backend.Network;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoldeMyPageInquiryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MoldeMyPageInquiryActivity.class.getSimpleName();
    @BindView(R.id.faqB)
    Button faqB;
    @BindView(R.id.edt_faq)
    EditText edt_faq; // 문의 내용
    @BindView(R.id.email)
    EditText email; // 이메일

    private static String email_ext;
    private String user_email;
    private Network network;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_inquiry);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText("문의하기");

        faqB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MoldeMyPageInquiryActivity.this, MoldeMyPageFaQActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.emailSelect).setOnClickListener(this);
        findViewById(R.id.emailEnter).setOnClickListener(this);


    }

    PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.naver:
                    email_ext = "naver.com";
                    Toast.makeText(MoldeMyPageInquiryActivity.this, "Naver", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.hanmail:
                    email_ext = "hanmail.net";
                    Toast.makeText(MoldeMyPageInquiryActivity.this, "Hanmail", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.gmail:
                    email_ext = "gmail.com";
                    Toast.makeText(MoldeMyPageInquiryActivity.this, "Gmail", Toast.LENGTH_SHORT).show();
                    break;
            }
            // 입력하고 나서 이메일 합쳐주기
            user_email = email.getText().toString() + email_ext;
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailSelect:
                PopupMenu popup = new PopupMenu(this, v);
                getMenuInflater().inflate(R.menu.emailmenu, popup.getMenu());
                popup.setOnMenuItemClickListener(listener);
                popup.show();//Popup Menu 보이기
                break;

            case R.id.emailEnter:
                // send Server FaQ
                sendFaqDataToServer();
              /*  if (RegexUtil.validateEmail(user_email)) {

                } else {
                    Toast.makeText(MoldeMyPageInquiryActivity.this, "이메일 양식에 맞지 않습니다. 확인해주세요", Toast.LENGTH_LONG).show();
                }
*/
                break;

        }
    }

    private void sendFaqDataToServer() {
        network = Network.getNetworkInstance();
        network.getFaqProxy().sendFaqDataToServer("사용자 토큰(Firebase 연동해라~~)",
                "사용자 SNS 계정",
                edt_faq.getText().toString(),
                "이메일 입력.. 요기 null뜸",
                new Callback<MoldeFaQ>() {
                    @Override
                    public void onResponse(Call<MoldeFaQ> call, Response<MoldeFaQ> response) {
                        if (response.isSuccessful()) {
                            // 전송 완료
                            Log.i(TAG, "전송 완료");
                        }
                    }

                    @Override
                    public void onFailure(Call<MoldeFaQ> call, Throwable t) {
                        Log.e(TAG, t.getMessage());
                    }
                });
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

