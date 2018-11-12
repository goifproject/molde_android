package com.limefriends.molde.screen.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.MenuItem;

import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.screen.common.bottomNavigationViewHelper.BottomNavigationViewHelper;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.viewController.BackPressDispatcher;
import com.limefriends.molde.screen.common.viewController.BackPressedListener;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.feed.main.FeedFragment;
import com.limefriends.molde.screen.magazine.main.CardNewsFragment;
import com.limefriends.molde.screen.map.main.MapFragment;
import com.limefriends.molde.screen.mypage.main.MyPageFragment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMainActivity extends BaseActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, BackPressDispatcher {

    private Set<BackPressedListener> mBackPressListeners = new HashSet<>();

    @Override
    public void registerListener(BackPressedListener listener) {
        mBackPressListeners.add(listener);
    }

    @Override
    public void unregisterListener(BackPressedListener listener) {
        mBackPressListeners.remove(listener);
    }

    public interface OnKeyBackPressedListener {
        void onBackKey();
    }

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    //private OnKeyBackPressedListener mOnKeyBackPressedListener;
    private SparseArrayCompat fragmentSparseArray;
    private FeedEntity feedEntity;
    private Fragment fragment;
    private long lastTimeBackPressed;

    @Service private BottomNavigationViewHelper mBottomNavigationViewHelper;
    @Service private ToastHelper mToastHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        fragmentSparseArray = new SparseArrayCompat();

        setupView();
    }

    private void setupView() {
        setContentView(R.layout.activity_main);
        setupWindowAnimations();
        ButterKnife.bind(this);
        mBottomNavigationViewHelper.disableShiftMode(navigation);
        setupListener();

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

//    public void setOnKeyBackPressedListener(OnKeyBackPressedListener listener) {
//        mOnKeyBackPressedListener = listener;
//    }

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

        boolean isBackPressConsumedByAnyListener = false;

        for (BackPressedListener listener : mBackPressListeners) {
            if (listener.onBackPressed()) {
                isBackPressConsumedByAnyListener = true;
            }
        }

        if (!isBackPressConsumedByAnyListener) {
            if (System.currentTimeMillis() > lastTimeBackPressed + 1500) {
                lastTimeBackPressed = System.currentTimeMillis();
                mToastHelper.showShortToast("한번 더 누르면 앱이 종료됩니다");
            } else {
                finishAfterTransition();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
