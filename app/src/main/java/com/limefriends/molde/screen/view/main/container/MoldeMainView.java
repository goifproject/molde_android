package com.limefriends.molde.screen.view.main.container;

import android.widget.FrameLayout;

import com.limefriends.molde.screen.common.view.ObservableView;

public interface MoldeMainView extends ObservableView<MoldeMainView.Listener> {

    public interface Listener {

        void onMagazineTabClicked();

        void onMapTabClicked();

        void onFeedTabClicked();

        void onMyPageTabClicked();
    }

    void changeTab(int tabId);

    FrameLayout getFragmentFrame();

}
