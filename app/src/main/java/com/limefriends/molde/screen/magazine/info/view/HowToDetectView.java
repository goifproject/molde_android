package com.limefriends.molde.screen.magazine.info.view;

import com.limefriends.molde.screen.common.views.ObservableView;

public interface HowToDetectView extends ObservableView<HowToDetectView.Listener> {

    public interface Listener {

        void onReportClicked();

        void onNavigateUpClicked();
    }

    void bindTitle(String title);
}
