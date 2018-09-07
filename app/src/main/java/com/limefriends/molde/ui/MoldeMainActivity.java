package com.limefriends.molde.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.MenuItem;

import com.limefriends.molde.R;
import com.limefriends.molde.comm.helper.BottomNavigationViewHelper;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.ui.feed.FeedFragment;
import com.limefriends.molde.ui.magazine.CardNewsFragment;
import com.limefriends.molde.ui.map.main.MapFragment;
import com.limefriends.molde.ui.mypage.MyPageFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    public interface OnKeyBackPressedListener {
        void onBackKey();
    }

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private OnKeyBackPressedListener mOnKeyBackPressedListener;
    private SparseArrayCompat fragmentSparseArray;
    private FeedEntity feedEntity;
    private Fragment fragment;
    private long lastTimeBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
    }

    private void setupView() {
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        ButterKnife.bind(this);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        setupListener();
        if (fragment == null && fragmentSparseArray == null) {
            fragmentSparseArray = new SparseArrayCompat();
            fragment = new MapFragment();
        }
        navigation.setSelectedItemId(R.id.main_menu_map);
    }

    private void setupListener() {
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private void setupWindowAnimations() {
        Slide slide = (Slide)
                TransitionInflater.from(this).inflateTransition(R.transition.activity_slide);
        getWindow().setExitTransition(slide);
    }

    public void setSelectedMenu(int fragmentId) {
        navigation.setSelectedItemId(fragmentId);
    }

    public void replaceFragment(Fragment fm) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_fragment, fm)
                .addToBackStack(null).commit();
    }

    public void setOnKeyBackPressedListener(OnKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_magazine:
                if (fragmentSparseArray.get(R.string.main_menu_magazine) == null) {
                    fragment = new CardNewsFragment();
                    fragmentSparseArray.append(R.string.main_menu_magazine, fragment);
                    replaceFragment(fragment);
                } else {
                    fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_magazine);
                    replaceFragment(fragment);
                }
                return true;
            case R.id.main_menu_map:
                if (fragmentSparseArray.get(R.string.main_menu_map) == null) {
                    fragment = new MapFragment();
                    fragmentSparseArray.append(R.string.main_menu_map, fragment);
                    replaceFragment(fragment);
                } else {
                    fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_map);
                    replaceFragment(fragment);
                }
                return true;
            case R.id.main_menu_feed:
                if (fragmentSparseArray.get(R.string.main_menu_report_list) == null) {
                    fragment = new FeedFragment();
                    fragmentSparseArray.append(R.string.main_menu_report_list, fragment);
                    replaceFragment(fragment);
                } else {
                    fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_report_list);
                    replaceFragment(fragment);
                }
                return true;
            case R.id.main_menu_mypage:
                if (fragmentSparseArray.get(R.string.main_menu_mypage) == null) {
                    fragment = new MyPageFragment();
                    fragmentSparseArray.append(R.string.main_menu_mypage, fragment);
                    replaceFragment(fragment);
                } else {
                    fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_mypage);
                    replaceFragment(fragment);
                }
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > lastTimeBackPressed + 1500) {
            lastTimeBackPressed = System.currentTimeMillis();
            if (mOnKeyBackPressedListener != null) {
                mOnKeyBackPressedListener.onBackKey();
            }
        } else {
            finishAfterTransition();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public FeedEntity getFeedEntity() {
        return feedEntity;
    }

    public void setFeedEntity(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
        ((MapFragment) fragmentSparseArray.get(R.string.main_menu_map)).setFromFeed(true);
    }
}
