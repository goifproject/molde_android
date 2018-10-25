package com.limefriends.molde.screen.common.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.limefriends.molde.common.DI.ControllerCompositionRoot;
import com.limefriends.molde.common.MoldeApplication;

public abstract class BaseFragment extends Fragment {

    private ControllerCompositionRoot mControllerCompositionRoot;

    public ControllerCompositionRoot getCompositionRoot() {
        if (mControllerCompositionRoot == null) {
            mControllerCompositionRoot = new ControllerCompositionRoot(
                    ((MoldeApplication) getActivity().getApplication()).getCompositionRoot(),
                    getActivity()
            );
        }
        return mControllerCompositionRoot;
    }

}
