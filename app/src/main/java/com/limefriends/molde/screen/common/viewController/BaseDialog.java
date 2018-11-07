package com.limefriends.molde.screen.common.viewController;


import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;

import com.limefriends.molde.common.di.CompositionRoot;
import com.limefriends.molde.common.di.Injector;
import com.limefriends.molde.common.di.PresentationCompositionRoot;

public abstract class BaseDialog extends DialogFragment {

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
        return new PresentationCompositionRoot(getAppCompositionRoot(), getActivity());
    }

    private CompositionRoot getAppCompositionRoot() {
        return new CompositionRoot();
    }

}
