package com.limefriends.molde.molde_sub_desc;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;
import com.pm10.library.CircleIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeSubDescActivity extends AppCompatActivity {
    private final int SUB_DESC_PAGE_CNT = 4;
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
            if(0 > position || SUB_DESC_PAGE_CNT <= position) return null;
            switch (position){
                case 0 :
                    return MoldeSubDescFirstFragment.newInstance();
                case 1 :
                    return MoldeSubDescSecondFragment.newInstance();
                case 2 :
                    return MoldeSubDescThirdFragment.newInstance();
                case 3 :
                    go_to_start_button.setVisibility(View.VISIBLE);
                    return MoldeSubDescForthFragment.newInstance();
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return SUB_DESC_PAGE_CNT;
        }
    }
}
