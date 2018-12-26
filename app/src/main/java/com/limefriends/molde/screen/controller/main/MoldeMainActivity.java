package com.limefriends.molde.screen.controller.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.widget.FrameLayout;

import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.fragmentFrameHelper.FragmentFrameWrapper;
import com.limefriends.molde.screen.common.screensNavigator.FragmentScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BackPressDispatcher;
import com.limefriends.molde.screen.common.viewController.BackPressedListener;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.controller.feed.main.FeedFragment;
import com.limefriends.molde.screen.controller.magazine.main.CardNewsFragment;
import com.limefriends.molde.screen.controller.map.main.MapFragment;
import com.limefriends.molde.screen.controller.mypage.main.MyPageFragment;
import com.limefriends.molde.screen.view.main.container.MoldeMainView;

import java.util.HashSet;
import java.util.Set;

public class MoldeMainActivity extends BaseActivity
        implements FragmentFrameWrapper, BackPressDispatcher, MoldeMainView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent(context, MoldeMainActivity.class);
        context.startActivity(intent);
    }

    private Set<BackPressedListener> mBackPressListeners = new HashSet<>();

    @Override
    public void registerListener(BackPressedListener listener) {
        mBackPressListeners.add(listener);
    }

    @Override
    public void unregisterListener(BackPressedListener listener) {
        mBackPressListeners.remove(listener);
    }


    private SparseArrayCompat fragmentSparseArray;
    private FeedEntity feedEntity;
    private Fragment fragment;
    private long lastTimeBackPressed;

    @Service private ToastHelper mToastHelper;
    @Service private ViewFactory mViewFactory;
    @Service private FragmentScreenNavigator mFragmentScreenNavigator;
    private MoldeMainView mMoldeMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mMoldeMainView = mViewFactory.newInstance(MoldeMainView.class, null);

        fragmentSparseArray = new SparseArrayCompat();

        mMoldeMainView.registerListener(this);

        mMoldeMainView.changeTab(R.id.main_menu_map);

        setContentView(mMoldeMainView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void changeTab(int fragmentId) {
        mMoldeMainView.changeTab(fragmentId);
    }

    public void replaceFragment(Fragment fm) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_fragment, fm)
                .addToBackStack(null).commit();
    }

    public FeedEntity getFeedEntity() {
        return feedEntity;
    }

    public void setFeedEntity(FeedEntity feedEntity) {
        this.feedEntity = feedEntity;
        ((MapFragment) fragmentSparseArray.get(R.string.main_menu_map)).setFromFeed(true);
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

    @Override
    public void onMagazineTabClicked() {
        if (fragmentSparseArray.get(R.string.main_menu_magazine) == null) {
            fragment = new CardNewsFragment();
            fragmentSparseArray.append(R.string.main_menu_magazine, fragment);
            replaceFragment(fragment);
        } else {
            fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_magazine);
            replaceFragment(fragment);
        }
    }

    @Override
    public void onMapTabClicked() {
        if (fragmentSparseArray.get(R.string.main_menu_map) == null) {
            fragment = new MapFragment();
            fragmentSparseArray.append(R.string.main_menu_map, fragment);
            replaceFragment(fragment);
        } else {
            fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_map);
            replaceFragment(fragment);
        }
    }

    @Override
    public void onFeedTabClicked() {
        if (fragmentSparseArray.get(R.string.main_menu_report_list) == null) {
            fragment = new FeedFragment();
            fragmentSparseArray.append(R.string.main_menu_report_list, fragment);
            replaceFragment(fragment);
        } else {
            fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_report_list);
            replaceFragment(fragment);
        }
    }

    @Override
    public void onMyPageTabClicked() {
        if (fragmentSparseArray.get(R.string.main_menu_mypage) == null) {
            fragment = new MyPageFragment();
            fragmentSparseArray.append(R.string.main_menu_mypage, fragment);
            replaceFragment(fragment);
        } else {
            fragment = (Fragment) fragmentSparseArray.get(R.string.main_menu_mypage);
            replaceFragment(fragment);
        }
    }

    @Override
    public FrameLayout getFragmentFrame() {
        return mMoldeMainView.getFragmentFrame();
    }
}
