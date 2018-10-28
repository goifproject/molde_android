package com.limefriends.molde.screen.magazine.info.view;

import com.limefriends.molde.screen.common.views.ObservableView;

public interface RecentMolcaInfoView extends ObservableView<RecentMolcaInfoView.Listener> {

    public interface Listener {

        void onReportClicked();
    }
}