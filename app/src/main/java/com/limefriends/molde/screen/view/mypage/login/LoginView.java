package com.limefriends.molde.screen.view.mypage.login;

import com.limefriends.molde.screen.common.view.ObservableView;

public interface LoginView extends ObservableView<LoginView.Listener> {

    public interface Listener {

        void onSkipLoginClicked();

        void onGoogleLoginClicked();
    }

    void showProgressIndication();

    void hideProgressIndication();
}
