package com.limefriends.molde.screen.magazine.info.view;

import com.limefriends.molde.screen.common.views.ObservableView;

public interface HowToRespondView extends ObservableView<HowToRespondView.Listener> {

    public interface Listener {

        void onReportClicked();
    }
}
