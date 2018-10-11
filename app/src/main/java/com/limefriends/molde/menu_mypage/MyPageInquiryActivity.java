package com.limefriends.molde.menu_mypage;

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
import android.widget.Spinner;
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
    @BindView(R.id.faq_email_self_input)
    EditText faq_email_self_input;
    @BindView(R.id.faq_email_select)
    Spinner faq_email_select;
    @BindView(R.id.faq_self_close_button)
    ImageButton faq_self_close_button;
    @BindView(R.id.faq_send_button)
    Button faq_send_button;

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
                Intent intent = new Intent(MyPageInquiryActivity.this, MoldeMyPageFaQActivity.class);
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
                if(faq_email_select.getVisibility() == View.GONE && faq_email_self_input.getVisibility() == View.VISIBLE) {
                    faqEmail = faq_email_input.getText().toString() + "@" + faq_email_self_input.toString();
                }else if(faq_email_self_input.getVisibility() == View.VISIBLE && faq_email_self_input.getVisibility() == View.GONE) {
                    faqEmail = faq_email_input.getText().toString() + "@" + faq_email_select.getSelectedItem().toString();
                }
                if (RegexUtil.validateEmail(faqEmail)) {
                    Retrofit retrofit = MoldeNetwork.getNetworkInstance().getRetrofit();
                    MoldeFaqRestService moldeFaqRestService = retrofit.create(MoldeFaqRestService.class);
                    Call<MoldeFaQ> moldeFaqCall = moldeFaqRestService.sendFaQData(
                            new MoldeFaQ(
                                    MoldeApplication.firebaseAuth.getUid(),
                                    MoldeApplication.firebaseAuth.getCurrentUser().getDisplayName(),
                                    faq_content.getText().toString(),
                                    faqEmail
                            )
                    );
                    moldeFaqCall.enqueue(new Callback<MoldeFaQ>() {
                        @Override
                        public void onResponse(Call<MoldeFaQ> call, Response<MoldeFaQ> response) {
                            if (response.isSuccessful()) {

                            }
                        }

                        @Override
                        public void onFailure(Call<MoldeFaQ> call, Throwable t) {
                            Log.e(TAG, t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(MyPageInquiryActivity.this, "이메일 양식에 맞지 않습니다. 확인해주세요", Toast.LENGTH_LONG).show();
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
}
