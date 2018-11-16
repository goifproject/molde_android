package com.limefriends.molde.screen.view.mypage.faq;

import com.limefriends.molde.screen.common.view.ObservableView;

public interface FaqView extends ObservableView<FaqView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();
    }
}
