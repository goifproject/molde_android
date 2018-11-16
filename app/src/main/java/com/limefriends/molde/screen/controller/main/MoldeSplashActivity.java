package com.limefriends.molde.screen.controller.main;

import android.os.Bundle;
import android.os.Handler;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.view.main.splash.MoldeSplashView;

import static com.limefriends.molde.common.Constant.Authority.*;

public class MoldeSplashActivity extends BaseActivity {

    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    @Service private ViewFactory mViewFactory;
    private MoldeSplashView mMoldeSplashView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mMoldeSplashView = mViewFactory.newInstance(MoldeSplashView.class, null);

        setContentView(mMoldeSplashView.getRootView());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean firstLaunch = PreferenceUtil.getBoolean(MoldeSplashActivity.this, "isFirst");
                boolean skipFirstTutorial = PreferenceUtil.getBoolean(MoldeSplashActivity.this, "skipFirst");
                boolean skipSecondTutorial = PreferenceUtil.getBoolean(MoldeSplashActivity.this, "skipSecond");

                if (firstLaunch) {
                    PreferenceUtil.putLong(MoldeSplashActivity.this, "authority", GUEST);
                    PreferenceUtil.putBoolean(MoldeSplashActivity.this, "isFirst", false);
                }

                if (skipFirstTutorial) {
                    if (skipSecondTutorial) {
                        mActivityScreenNavigator.toMoldeMainActivity();
                    } else {
                        mActivityScreenNavigator.toSubTutorialActivity();
                    }
                } else {
                    mActivityScreenNavigator.toTutorialActivity();
                }
                finish();
            }
        }, 1000);

    }
}
