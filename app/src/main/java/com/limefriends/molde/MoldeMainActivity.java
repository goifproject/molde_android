package com.limefriends.molde;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.limefriends.molde.menu_magazine.MoldeMagazineFragment;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapHistoryEntity;
import com.limefriends.molde.menu_map.entity.MoldeSearchMapInfoEntity;
import com.limefriends.molde.menu_map.MoldeMapFragment;
import com.limefriends.molde.menu_mypage.MoldeMyPageFragment;
import com.limefriends.molde.menu_feed.MoldeFeedFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    public static Context allContext;
    public SparseArrayCompat fragmentSparseArray;

    private long lastTimeBackPressed;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private MoldeSearchMapInfoEntity searchEntity;
    private MoldeSearchMapHistoryEntity historyEntity;
    private Fragment fragment;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_menu_magazine:
                    if(fragmentSparseArray.get(R.string.main_menu_magazine) == null) {
                        fragment = MoldeMagazineFragment.newInstance();
                        fragmentSparseArray.append(R.string.main_menu_magazine, fragment);
                        replaceFragment(fragment);
                    }else{
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_magazine);
                        replaceFragment(fragment);
                    }
                    return true;
                case R.id.main_menu_map:
                    if(fragmentSparseArray.get(R.string.main_menu_map) == null){
                        fragment = MoldeMapFragment.newInstance();
                        fragmentSparseArray.append(R.string.main_menu_map, fragment);
                        replaceFragment(fragment);
                    }else{
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_map);
                        replaceFragment(fragment);
                    }
                    return true;
                case R.id.main_menu_feed:
                    if(fragmentSparseArray.get(R.string.main_menu_report_list) == null) {
                        fragment = MoldeFeedFragment.newInstance();
                        fragmentSparseArray.append(R.string.main_menu_report_list, fragment);
                        replaceFragment(fragment);
                    }else{
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_report_list);
                        replaceFragment(fragment);
                    }
                    return true;
                case R.id.main_menu_mypage:
                    if(fragmentSparseArray.get(R.string.main_menu_mypage) == null) {
                        fragment = MoldeMyPageFragment.newInstance();
                        fragmentSparseArray.append(R.string.main_menu_mypage, fragment);
                        replaceFragment(fragment);
                    }else{
                        fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_mypage);
                        replaceFragment(fragment);
                    }
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
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if(fragment == null && fragmentSparseArray == null) {
            fragmentSparseArray = new SparseArrayCompat();
            fragment = MoldeMapFragment.newInstance();
            fragmentSparseArray.append(R.string.main_menu_map, fragment);
        }
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.menu_fragment, fragment).commit();
        navigation.setSelectedItemId(R.id.main_menu_map);
        allContext = this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (fragment != null && fragment instanceof MoldeMapFragment) {
            ((MoldeMapFragment) fragment).onPermissionCheck(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    public void replaceFragment(Fragment fm) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.menu_fragment, fm).addToBackStack(null).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent != null){
            searchEntity = (MoldeSearchMapInfoEntity) intent.getSerializableExtra("mapInfo");
            historyEntity = (MoldeSearchMapHistoryEntity) intent.getSerializableExtra("mapHistoryInfo");
        }
    }

    public MoldeSearchMapInfoEntity getMapInfoResultData() {
        return this.searchEntity;
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
            if (mOnKeyBackPressedListener != null) {
                mOnKeyBackPressedListener.onBackKey();
            }
            if(MoldeApplication.firebaseAuth.getUid() != null){
                Log.e("Auth", MoldeApplication.firebaseAuth.getUid());
                Log.e("User name", MoldeApplication.firebaseAuth.getCurrentUser().getDisplayName());
            }else {
                Log.e("Auth", "계정 UID값 없음");
                Log.e("User name", "계정 이름 없음");
            }

        } else {
            finish();
        }
    }
}
