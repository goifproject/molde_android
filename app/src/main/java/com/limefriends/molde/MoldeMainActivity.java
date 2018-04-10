package com.limefriends.molde;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.limefriends.molde.menu_magazine.MoldeMagazineFragment;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapHistoryEntity;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapInfoEntity;
import com.limefriends.molde.menu_map.MoldeMapFragment;
import com.limefriends.molde.menu_mypage.MoldeMyPageFragment;
import com.limefriends.molde.menu_reportlist.MoldeReportListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMainActivity extends AppCompatActivity {
    public static Context allContext;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private long lastTimeBackPressed;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private MoldeSearchMapInfoEntity entity;
    private MoldeSearchMapHistoryEntity historyEntity;
    private Fragment fragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_menu_map:
                    fragment = MoldeMapFragment.newInstance();
                    replaceFragment(fragment);
                    return true;
                case R.id.main_menu_magazine:
                    replaceFragment(MoldeMagazineFragment.newInstance());
                    return true;
                case R.id.main_menu_reportlist:
                    replaceFragment(MoldeReportListFragment.newInstance());
                    return true;
                case R.id.main_menu_mypage:
                    replaceFragment(MoldeMyPageFragment.newInstance());
                    return true;
            }
            return false;
        }

    };
    onKeyBackPressedListener mOnKeyBackPressedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molde_main);
        ButterKnife.bind(this);

        fragment = MoldeMapFragment.newInstance();
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.menu_fragment, fragment).commit();
        allContext = this;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (fragment != null && fragment instanceof MoldeMapFragment) {
            ((MoldeMapFragment) fragment).onPermissionCheck(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    private void replaceFragment(Fragment fm) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.menu_fragment, fm).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        entity = (MoldeSearchMapInfoEntity) intent.getSerializableExtra("mapInfo");
        historyEntity = (MoldeSearchMapHistoryEntity) intent.getSerializableExtra("mapHistoryInfo");

    }

    public MoldeSearchMapInfoEntity getMapInfoResultData() {
        return this.entity;
    }

    public MoldeSearchMapHistoryEntity getMapHistoryResultData() {
        return this.historyEntity;
    }

    public interface onKeyBackPressedListener {
        void onBackKey();
    }

    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > lastTimeBackPressed + 1500) {
            lastTimeBackPressed = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료", Toast.LENGTH_SHORT);
            if (mOnKeyBackPressedListener != null) {
                mOnKeyBackPressedListener.onBackKey();
                return;
            }
            return;
        } else {
            finish();
        }
    }
}
