package com.limefriends.molde.screen.common.fragmentFrameHelper;

import android.support.v4.app.Fragment;

import io.reactivex.annotations.Nullable;

public interface HierarchicalFragment {

    @Nullable
    Fragment getHierarchicalParentFragment();

}
