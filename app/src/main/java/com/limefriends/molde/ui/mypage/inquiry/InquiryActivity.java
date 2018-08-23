package com.limefriends.molde.ui.mypage.inquiry;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.comm.utils.pattern.RegexUtil;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.response.Result;
import com.limefriends.molde.remote.MoldeRestfulService;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.ui.mypage.faq.FaQActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO "lkj" 변경할 것
public class InquiryActivity extends AppCompatActivity {

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
    @BindView(R.id.inquire_container)
    RelativeLayout inquire_container;

    private String faqEmail;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_inquiry);

        setupViews();

        setupListener();
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title
                = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(getText(R.string.inquire));
    }

    private void setupListener() {
        // 자주 묻는 질문
        faq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InquiryActivity.this, FaQActivity.class);
                startActivity(intent);
            }
        });

        ArrayAdapter emailArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.email_select, android.R.layout.simple_spinner_item);
        faq_email_select.setAdapter(emailArrayAdapter);
        faq_email_select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    faq_email_select.setSelected(false);
                    return;
                }
                if (position == faq_email_select.getAdapter().getCount() - 1) {
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

        // 뒤로가기
        faq_self_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager
                        = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(faq_email_select.getWindowToken(), 0);
                faq_email_self_input.setVisibility(View.GONE);
                faq_self_close_button.setVisibility(View.GONE);
                faq_email_self_input.setText("");
                faq_email_select.setVisibility(View.VISIBLE);
                faq_email_select.bringToFront();
                faq_email_select.setSelection(0);
            }
        });

        // 전송하기
        faq_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (faq_email_select.getVisibility() == View.GONE &&
                        faq_email_self_input.getVisibility() == View.VISIBLE) {
                    faqEmail = faq_email_input.getText().toString()
                            + "@" + faq_email_self_input.toString();
                } else if (faq_email_select.getVisibility() == View.VISIBLE &&
                        faq_email_self_input.getVisibility() == View.GONE) {
                    faqEmail = faq_email_input.getText().toString()
                            + "@" + faq_email_select.getSelectedItem().toString();
                }
                FirebaseAuth auth = ((MoldeApplication) getApplication()).getFireBaseAuth();
                FirebaseUser user = auth.getCurrentUser();
                String faqContent = faq_content.getText().toString();

                if (faqContent.equals("")) {
                    snackBar(getText(R.string.snackbar_content_absent).toString());
                    return;
                }
                if (!faq_email_input.getText().toString().equals("")) {
                    if (!RegexUtil.validateEmail(faqEmail)) {
                        snackBar(getText(R.string.snackbar_invalid_email).toString());
                        return;
                    }
                } else {
                    faqEmail = user.getEmail();
                }
                if (auth.getCurrentUser().getDisplayName() == null) {
                    userName = "무명";
                } else {
                    userName = auth.getCurrentUser().getDisplayName();
                }
                inquire(auth.getUid(), userName, faqContent, faqEmail);
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

    private void snackBar(String message) {
        Snackbar.make(inquire_container, message, Snackbar.LENGTH_SHORT).show();
    }

    //-----
    // Network
    //-----

    private void inquire(String userId, String userName, String content, String email) {
        MoldeRestfulService.Faq faqService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Faq.class);
        Call<Result> call = faqService.createNewFaq("lkj", "이기정", content, email);

        progressBar.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    snackBar(getText(R.string.snackbar_inquire_accepted).toString());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }

}

