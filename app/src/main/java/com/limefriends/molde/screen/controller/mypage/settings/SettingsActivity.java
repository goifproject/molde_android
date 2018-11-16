package com.limefriends.molde.screen.controller.mypage.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.view.mypage.settings.SettingsView;

import static com.limefriends.molde.common.Constant.Common.ALLOW_PUSH;
import static com.limefriends.molde.common.Constant.Common.DISALLOW_PUSH;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_FEED_CHANGE_PUSH;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_NEW_FEED_PUSH;

public class SettingsActivity extends BaseActivity implements SettingsView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Service private ViewFactory mViewFactory;
    @Service private ToastHelper mToastHelper;
    private SettingsView mSettingsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mSettingsView = mViewFactory.newInstance(SettingsView.class, null);

        setContentView(mSettingsView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSettingsView.registerListener(this);

        int allowNewFeedPush =  PreferenceUtil.getInt(SettingsActivity.this, PREF_KEY_NEW_FEED_PUSH, DISALLOW_PUSH);
        int allowFeedChangePush = PreferenceUtil.getInt(SettingsActivity.this, PREF_KEY_FEED_CHANGE_PUSH, DISALLOW_PUSH);

        if (allowNewFeedPush == ALLOW_PUSH) {
            mSettingsView.bindFavoritePush(true);
        }
        if (allowFeedChangePush == ALLOW_PUSH) {
            mSettingsView.bindFeedPush(true);
        }
    }

    @Override
    public void onNavigateUpClicked() {
        onBackPressed();
    }

    @Override
    public void onFavoritePushCheckChanged(boolean isChecked) {
        if (isChecked) {
            PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_NEW_FEED_PUSH, ALLOW_PUSH);
            mSettingsView.showSnackBar("새 피드 푸쉬알람이 설정되었습니다.");
        } else {
            PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_NEW_FEED_PUSH, DISALLOW_PUSH);
            mSettingsView.showSnackBar("새 피드 푸쉬알람이 해제되었습니다.");
        }
    }

    @Override
    public void onFeedChangePushCheckChanged(boolean isChecked) {
        if (isChecked) {
            PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_FEED_CHANGE_PUSH, ALLOW_PUSH);
            mSettingsView.showSnackBar("신고 상태변화 푸쉬알람이 설정되었습니다.");
        } else {
            PreferenceUtil.putInt(SettingsActivity.this, PREF_KEY_FEED_CHANGE_PUSH, DISALLOW_PUSH);
            mSettingsView.showSnackBar("신고 상태변화 푸쉬알람이 해제되었습니다.");
        }
    }

}