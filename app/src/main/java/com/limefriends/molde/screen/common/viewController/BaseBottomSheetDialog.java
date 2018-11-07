package com.limefriends.molde.screen.common.viewController;

import android.support.annotation.UiThread;
import android.support.design.widget.BottomSheetDialogFragment;

import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.common.di.CompositionRoot;
import com.limefriends.molde.common.di.Injector;
import com.limefriends.molde.common.di.PresentationCompositionRoot;

public class BaseBottomSheetDialog extends BottomSheetDialogFragment {

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
