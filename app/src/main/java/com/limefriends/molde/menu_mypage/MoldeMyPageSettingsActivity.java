package com.limefriends.molde.menu_mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMyPageSettingsActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.mypage_made_by) Button mypage_made_by;
    @BindView(R.id.mypage_made_by_answer) TextView mypage_made_by_answer;
    @BindView(R.id.mypage_provisions) Button mypage_provisions;
    @BindView(R.id.mypage_provisions_answer) TextView mypage_provisions_answer;
    @BindView(R.id.mypage_license) Button mypage_license;
    @BindView(R.id.mypage_license_answer) TextView mypage_license_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_activity_settings);
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


        findViewById(R.id.mypage_made_by).setOnClickListener(this);
        findViewById(R.id.mypage_provisions).setOnClickListener(this);
        findViewById(R.id.mypage_license).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mypage_made_by:
                mypage_made_by_answer.setVisibility(View.VISIBLE);
                mypage_provisions_answer.setVisibility(View.GONE);
                mypage_license_answer.setVisibility(View.GONE);
                break;

            case R.id.mypage_provisions:
                mypage_made_by_answer.setVisibility(View.GONE);
                mypage_provisions_answer.setVisibility(View.VISIBLE);
                mypage_license_answer.setVisibility(View.GONE);
                break;

            case R.id.mypage_license:
                mypage_made_by_answer.setVisibility(View.GONE);
                mypage_provisions_answer.setVisibility(View.GONE);
                mypage_license_answer.setVisibility(View.VISIBLE);
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