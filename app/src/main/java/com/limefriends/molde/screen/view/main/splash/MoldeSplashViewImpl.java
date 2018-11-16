package com.limefriends.molde.screen.view.main.splash;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.view.BaseView;

public class MoldeSplashViewImpl extends BaseView implements MoldeSplashView {

    public MoldeSplashViewImpl(LayoutInflater inflater,
                               ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_splash, parent, false));
    }

}
