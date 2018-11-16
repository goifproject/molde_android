package com.limefriends.molde.screen.view.mypage.settings;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

public class SettingsViewImpl
        extends BaseObservableView<SettingsView.Listener> implements SettingsView, View.OnClickListener {


    private Button mypage_made_by;
    private LinearLayout mypage_made_by_answer;
    private Button mypage_provisions;
    private LinearLayout mypage_provisions_answer;
    private Button mypage_license;
    private TextView mypage_license_answer;
    private Button mypage_version_info;
    private TextView mypage_version_answer;
    private Switch switch_my_favorite_push;
    private Switch switch_feed_change_push;
    private RelativeLayout settings_container;

    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;
    private boolean isFavoritePushChecked = false;
    private boolean isFeedChangePushChecked = false;

    private ToastHelper mToastHelper;
    private ViewFactory mViewFactory;

    public SettingsViewImpl(LayoutInflater inflater,
                            ViewGroup parent,
                            ViewFactory viewFactory,
                            ToastHelper toastHelper) {

        this.mToastHelper = toastHelper;
        this.mViewFactory = viewFactory;

        setRootView(inflater.inflate(R.layout.activity_settings, parent, false));

        setupViews();

        setupToolbar();

        setupListener();
    }

    private void setupViews() {

        mToolbar = findViewById(R.id.toolbar);
        mypage_made_by = findViewById(R.id.mypage_made_by);
        mypage_made_by_answer = findViewById(R.id.mypage_made_by_answer);
        mypage_provisions = findViewById(R.id.mypage_provisions);
        mypage_provisions_answer = findViewById(R.id.mypage_provisions_answer);
        mypage_license = findViewById(R.id.mypage_license);
        mypage_license_answer = findViewById(R.id.mypage_license_answer);
        mypage_version_info = findViewById(R.id.mypage_version_info);
        mypage_version_answer = findViewById(R.id.mypage_version_answer);
        switch_my_favorite_push = findViewById(R.id.switch_favorite_push);
        switch_feed_change_push = findViewById(R.id.switch_feed_change_push);
        settings_container = findViewById(R.id.settings_container);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
        mNestedToolbar.setTitle(getContext().getText(R.string.settings).toString());
    }

    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        switch_my_favorite_push.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (Listener listener : getListeners()) {
                listener.onFavoritePushCheckChanged(isChecked);
            }
        });

        switch_feed_change_push.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (Listener listener : getListeners()) {
                listener.onFeedChangePushCheckChanged(isChecked);
            }
        });

        mypage_version_info.setOnClickListener(this);
        mypage_made_by.setOnClickListener(this);
        mypage_provisions.setOnClickListener(this);
        mypage_license.setOnClickListener(this);
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
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(settings_container, message);
    }

    @Override
    public void bindFavoritePush(boolean isChecked) {
        switch_my_favorite_push.setChecked(isChecked);
    }

    @Override
    public void bindFeedPush(boolean isChecked) {
        switch_feed_change_push.setChecked(isChecked);
    }
}
