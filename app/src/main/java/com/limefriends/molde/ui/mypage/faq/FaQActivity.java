package com.limefriends.molde.ui.mypage.faq;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FaQActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.faq1)
    TextView faq1;
    @BindView(R.id.faq2)
    TextView faq2;
    @BindView(R.id.faq3)
    TextView faq3;
    @BindView(R.id.faq4)
    TextView faq4;
    @BindView(R.id.faq5)
    TextView faq5;
    @BindView(R.id.answer1)
    TextView answer1;
    @BindView(R.id.answer2)
    TextView answer2;
    @BindView(R.id.answer3)
    TextView answer3;
    @BindView(R.id.answer4)
    TextView answer4;
    @BindView(R.id.answer5)
    TextView answer5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        setupViews();
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title
                = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(getText(R.string.faq));

        findViewById(R.id.faq1).setOnClickListener(this);
        findViewById(R.id.faq2).setOnClickListener(this);
        findViewById(R.id.faq3).setOnClickListener(this);
        findViewById(R.id.faq4).setOnClickListener(this);
        findViewById(R.id.faq5).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faq1:
                answer1.setVisibility(View.VISIBLE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq2:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.VISIBLE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq3:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.VISIBLE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq4:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.VISIBLE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq5:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.VISIBLE);
                break;
        }
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
