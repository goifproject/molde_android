package com.limefriends.molde.screen.mypage.login.view;

import com.limefriends.molde.screen.common.view.ObservableView;

public interface LoginView extends ObservableView<LoginView.Listener> {

    public interface Listener {

        void onSkipLoginClicked();

        void onGoogleLoginClicked();
    }

    void showProgressIndication();

    void hideProgressIndication();
}
