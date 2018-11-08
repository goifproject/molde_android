package com.limefriends.molde.screen.mypage.faq.view;

import com.limefriends.molde.screen.common.view.ObservableView;

public interface FaqView extends ObservableView<FaqView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();
    }
}
