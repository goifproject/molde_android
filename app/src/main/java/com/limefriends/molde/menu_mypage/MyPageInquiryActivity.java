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

import com.limefriends.molde.MoldeApplication;
import com.limefriends.molde.Pattern.RegexUtil;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_mypage.faq.MoldeFaQ;
import com.limefriends.molde.menu_mypage.faq.MoldeFaqRestService;
import com.limefriends.molde.menu_mypage.faq.MoldeMyPageFaQActivity;
import com.limefriends.molde.molde_backend.MoldeNetwork;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyPageInquiryActivity extends AppCompatActivity {

    private static final String TAG = MyPageInquiryActivity.class.getSimpleName();
    @BindView(R.id.faq_button)
    Button faq_button;
    @BindView(R.id.faq_content)
    EditText faq_content;
    @BindView(R.id.faq_email_input)
    EditText faq_email_input;
    @BindView(R.id.faq_email_select_button)
    Button faq_email_select_button;
    @BindView(R.id.faq_send_button)
    Button faq_send_button;

    private static String emailExt;
    private String userEmail;


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

        faq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageInquiryActivity.this, MoldeMyPageFaQActivity.class);
                startActivity(intent);
            }
        });


        faq_email_select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.emailmenu, popup.getMenu());
                popup.setOnMenuItemClickListener(listener);
                popup.show();
            }
        });
        faq_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RegexUtil.validateEmail(userEmail)) {
                    sendFaqData();
                } else {
                    Toast.makeText(MyPageInquiryActivity.this, "이메일 양식에 맞지 않습니다. 확인해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    PopupMenu.OnMenuItemClickListener listener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.naver:
                    emailExt = "naver.com";
                    Toast.makeText(MyPageInquiryActivity.this, "Naver", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.hanmail:
                    emailExt = "hanmail.net";
                    Toast.makeText(MyPageInquiryActivity.this, "Hanmail", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.gmail:
                    emailExt = "gmail.com";
                    Toast.makeText(MyPageInquiryActivity.this, "Gmail", Toast.LENGTH_SHORT).show();
                    break;
            }
            // 입력하고 나서 이메일 합쳐주기
            userEmail = faq_email_input.getText().toString() + emailExt;
            return false;
        }
    };

    private void sendFaqData() {
        Retrofit retrofit = MoldeNetwork.getNetworkInstance().getRetrofit();
        MoldeFaqRestService moldeFaqRestService = retrofit.create(MoldeFaqRestService.class);
        Call<MoldeFaQ> moldeFaqCall = moldeFaqRestService.sendFaQ(
                new MoldeFaQ(
                        MoldeApplication.firebaseAuth.getUid(),
                        MoldeApplication.firebaseAuth.getCurrentUser().getDisplayName(),
                        faq_content.getText().toString(),
                        userEmail
                )
        );
        moldeFaqCall.enqueue(new Callback<MoldeFaQ>() {
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

