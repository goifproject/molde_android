package com.limefriends.molde.ui.mypage.settings;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.PreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.comm.Constant.Common.ALLOW_PUSH;
import static com.limefriends.molde.comm.Constant.Common.DISALLOW_PUSH;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_FEED_CHANGE_PUSH;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_NEW_FEED_PUSH;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.mypage_made_by)
    Button mypage_made_by;
    @BindView(R.id.mypage_made_by_answer)
    LinearLayout mypage_made_by_answer;
    @BindView(R.id.mypage_provisions)
    Button mypage_provisions;
    @BindView(R.id.mypage_provisions_answer)
    TextView mypage_provisions_answer;
    @BindView(R.id.mypage_license)
    Button mypage_license;
    @BindView(R.id.mypage_license_answer)
    TextView mypage_license_answer;
    @BindView(R.id.mypage_version_info)
    Button mypage_version_info;
    @BindView(R.id.mypage_version_answer)
    TextView mypage_version_answer;
    @BindView(R.id.switch_favorite_push)
    Switch switch_my_favorite_push;
    @BindView(R.id.switch_feed_change_push)
    Switch switch_feed_change_push;
    @BindView(R.id.settings_container)
    RelativeLayout settings_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(getText(R.string.settings));

        findViewById(R.id.mypage_version_info).setOnClickListener(this);
        findViewById(R.id.mypage_made_by).setOnClickListener(this);
        findViewById(R.id.mypage_provisions).setOnClickListener(this);
        findViewById(R.id.mypage_license).setOnClickListener(this);

        switch_my_favorite_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_NEW_FEED_PUSH, ALLOW_PUSH);
                    snack("새 피드 푸쉬알람이 설정되었습니다.");
                } else {
                    PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_NEW_FEED_PUSH, DISALLOW_PUSH);
                    snack("새 피드 푸쉬알람이 해제되었습니다.");
                }
            }
        });
        switch_feed_change_push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_FEED_CHANGE_PUSH, ALLOW_PUSH);
                    snack("신고 상태변화 푸쉬알람이 설정되었습니다.");
                } else {
                    PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_FEED_CHANGE_PUSH, DISALLOW_PUSH);
                    snack("신고 상태변화 푸쉬알람이 해제되었습니다.");
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mypage_version_info:
                mypage_version_answer.setVisibility(View.VISIBLE);
                mypage_made_by_answer.setVisibility(View.GONE);
                mypage_provisions_answer.setVisibility(View.GONE);
                mypage_license_answer.setVisibility(View.GONE);
                break;
            case R.id.mypage_made_by:
                mypage_version_answer.setVisibility(View.GONE);
                mypage_made_by_answer.setVisibility(View.VISIBLE);
                mypage_provisions_answer.setVisibility(View.GONE);
                mypage_license_answer.setVisibility(View.GONE);
                break;

            case R.id.mypage_provisions:
                mypage_version_answer.setVisibility(View.GONE);
                mypage_made_by_answer.setVisibility(View.GONE);
                mypage_provisions_answer.setVisibility(View.VISIBLE);
                mypage_license_answer.setVisibility(View.GONE);
                break;

            case R.id.mypage_license:
                mypage_version_answer.setVisibility(View.GONE);
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

    private void snack(String message) {
        Snackbar.make(settings_container, message, Snackbar.LENGTH_SHORT).show();
    }

}