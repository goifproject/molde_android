package com.limefriends.molde.ui.molde_sub_desc;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.pm10.library.CircleIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeSubDescActivity extends AppCompatActivity {
    private final int SUB_DESC_PAGE_CNT = 4;

    //로그인 안하고 건너뜀
    private static final int SKIP_LOGIN_CODE = 1001;
    //구글 로그인 완료
    private static final int CONNECT_GOOGLE_AUTH_CODE = 1002;
    //페북 로그인 완료
    private static final int CONNECT_FACEBOOK_AUTH_CODE = 1003;

    @BindView(R.id.molde_sub_desc_pager)
    ViewPager molde_sub_desc_pager;
    @BindView(R.id.sub_desc_indicator)
    CircleIndicator sub_desc_indicator;
    @BindView(R.id.go_to_start_button)
    Button go_to_start_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molde_sub_desc);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        switch (intent.getIntExtra("authLoginDone", SKIP_LOGIN_CODE)) {
            case CONNECT_GOOGLE_AUTH_CODE:
                Snackbar.make(findViewById(R.id.sub_desc_layout), "구글 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show();
                break;
            case CONNECT_FACEBOOK_AUTH_CODE:
                Snackbar.make(findViewById(R.id.sub_desc_layout), "페이스북 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show();
                break;
            case SKIP_LOGIN_CODE:
                break;
        }
        molde_sub_desc_pager.setAdapter(new MoldeSubDescAdapter(getSupportFragmentManager()));
        sub_desc_indicator.setupWithViewPager(molde_sub_desc_pager);
        go_to_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MoldeMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private class MoldeSubDescAdapter extends FragmentPagerAdapter {
        public MoldeSubDescAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (0 > position || SUB_DESC_PAGE_CNT <= position) return null;
            switch (position) {
                case 0:
                    return MoldeSubDescFirstFragment.newInstance();
                case 1:
                    return MoldeSubDescSecondFragment.newInstance();
                case 2:
                    return MoldeSubDescThirdFragment.newInstance();
                case 3:
                    go_to_start_button.setVisibility(View.VISIBLE);
                    return MoldeSubDescForthFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return SUB_DESC_PAGE_CNT;
        }
    }
}
