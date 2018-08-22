package com.limefriends.molde.ui.menu_mypage.inquiry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.utils.pattern.RegexUtil;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.menu_mypage.faq.FaQActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InquiryActivity extends AppCompatActivity {

    private static final String TAG = InquiryActivity.class.getSimpleName();
    @BindView(R.id.faq_button)
    Button faq_button;
    @BindView(R.id.faq_content)
    EditText faq_content;
    @BindView(R.id.faq_email_input)
    EditText faq_email_input;
    @BindView(R.id.faq_email_self_input)
    EditText faq_email_self_input;
    @BindView(R.id.faq_email_select)
    Spinner faq_email_select;
    @BindView(R.id.faq_self_close_button)
    ImageButton faq_self_close_button;
    @BindView(R.id.faq_send_button)
    Button faq_send_button;
    @BindView(R.id.inquiry_progress)
    ProgressBar progressBar;

    private String faqEmail;

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
                Intent intent = new Intent(InquiryActivity.this, FaQActivity.class);
                startActivity(intent);
            }
        });

        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        final ArrayAdapter emailArrayAdapter = ArrayAdapter.createFromResource(this, R.array.email_select, android.R.layout.simple_spinner_item);
        faq_email_select.setAdapter(emailArrayAdapter);
        faq_email_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    faq_email_select.setSelected(false);
                    return;
                }
                if (position == emailArrayAdapter.getCount() - 1) {
                    faq_email_select.setVisibility(View.GONE);
                    faq_email_self_input.setVisibility(View.VISIBLE);
                    faq_self_close_button.setVisibility(View.VISIBLE);
                    faq_self_close_button.bringToFront();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        faq_self_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faq_email_self_input.setVisibility(View.GONE);
                faq_self_close_button.setVisibility(View.GONE);
                faq_email_self_input.setText("");
                inputMethodManager.hideSoftInputFromWindow(faq_email_select.getWindowToken(), 0);
                faq_email_select.setVisibility(View.VISIBLE);
                faq_email_select.bringToFront();
                faq_email_select.setSelection(0);
            }
        });

        faq_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (faq_email_select.getVisibility() == View.GONE && faq_email_self_input.getVisibility() == View.VISIBLE) {
                    faqEmail = faq_email_input.getText().toString() + "@" + faq_email_self_input.toString();
                } else if (faq_email_select.getVisibility() == View.VISIBLE && faq_email_self_input.getVisibility() == View.GONE) {
                    faqEmail = faq_email_input.getText().toString() + "@" + faq_email_select.getSelectedItem().toString();
                }

                // TODO 로그인 처리를 한 후 다시 작업
                FirebaseAuth firebaseAuth = ((MoldeApplication) getApplication()).getFireBaseAuth();

                String faqContent = faq_content.getText().toString();

                if (faqEmail.equals("")) {
                    toast("이메일 주소가 없잖아 임마. 신고가 장난이냐");
                    return;
                }

                if (faqContent.equals("")) {
                    toast("내용이 없잖아 임마. 문의가 장난이냐");
                    return;
                }

                if (RegexUtil.validateEmail(faqEmail)) {

                    MoldeRestfulService.Faq faqService
                            = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Faq.class);

                    // TODO 로그인 할 때 받아놓고 없으면 로그인 페이지로 넘어가도록 해야 한다.

                    Call<Result> call = faqService.createNewFaq(
                            "lkj",
                            "이기정",
                            faqContent,
                            faqEmail
                    );

                    progressBar.setVisibility(View.VISIBLE);

                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "데이터 전송 성공", Toast.LENGTH_SHORT).show();
                                Log.e("로그 자 보자", response.body().getResult() + "");
                                // TODO 여기서 finish() 잠시 두고 3-5대 열어놓고 연속으로 누르면서 부하를 얼만큼 버티는지 알아보자
                                progressBar.setVisibility(View.INVISIBLE);
                                toast("문의가 접수됬습니다. 감사합니다");
                                finish();
                            } else {
                                try {
                                    Log.e("로그 자 보자", response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {

                        }
                    });

                } else {
                    Toast.makeText(InquiryActivity.this, "이메일 양식에 맞지 않습니다. 확인해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}

