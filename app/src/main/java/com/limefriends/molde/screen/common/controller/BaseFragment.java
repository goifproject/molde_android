package com.limefriends.molde.screen.common.controller;

import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;

import com.limefriends.molde.common.di.CompositionRoot;
import com.limefriends.molde.common.di.Injector;
import com.limefriends.molde.common.di.PresentationCompositionRoot;
import com.limefriends.molde.common.app.MoldeApplication;

public abstract class BaseFragment extends Fragment {

    private boolean mIsInjectorUsed;

    @UiThread
    protected Injector getInjector() {
        if (mIsInjectorUsed) {
            throw  new RuntimeException("there is no need to user more than one injector");
        }
        return new Injector(getCompositionRoot());
    }

    public PresentationCompositionRoot getCompositionRoot() {
        return new PresentationCompositionRoot(getAppCompositionRoot(), getActivity());
    }

    private CompositionRoot getAppCompositionRoot() {
        return  ((MoldeApplication) getActivity().getApplication()).getCompositionRoot();
    }

}
