package com.limefriends.molde.screen.common.controller;

import android.support.v7.app.AppCompatActivity;

import com.limefriends.molde.common.DI.ControllerCompositionRoot;
import com.limefriends.molde.common.MoldeApplication;

public abstract class BaseActivity extends AppCompatActivity {

    private ControllerCompositionRoot mControllerCompositionRoot;

    public ControllerCompositionRoot getCompositionRoot() {
        if (mControllerCompositionRoot == null) {
            mControllerCompositionRoot = new ControllerCompositionRoot(
                    ((MoldeApplication)getApplication()).getCompositionRoot(),
                    this
            );
        }
        return mControllerCompositionRoot;
    }

}
