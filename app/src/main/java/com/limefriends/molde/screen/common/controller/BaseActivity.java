package com.limefriends.molde.screen.common.controller;

import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;

import com.limefriends.molde.common.di.CompositionRoot;
import com.limefriends.molde.common.di.Injector;
import com.limefriends.molde.common.di.PresentationCompositionRoot;
import com.limefriends.molde.common.MoldeApplication;

public abstract class BaseActivity extends AppCompatActivity {

    private boolean mIsInjectorUsed;

    @UiThread
    protected Injector getInjector() {
        if (mIsInjectorUsed) {
            throw new RuntimeException("there is no need to use injector more than once");
        }
        mIsInjectorUsed = true;
        return new Injector(getCompositionRoot());
    }

    private PresentationCompositionRoot getCompositionRoot() {
        return new PresentationCompositionRoot(getAppCompositionRoot(), this);
    }

    private CompositionRoot getAppCompositionRoot() {
        return ((MoldeApplication)getApplication()).getCompositionRoot();
    }

}
