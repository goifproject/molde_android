package com.limefriends.molde.screen.view.mypage.main;

import android.net.Uri;

import com.limefriends.molde.screen.common.view.ObservableView;

public interface MyPageMainView extends ObservableView<MyPageMainView.Listener> {

    public interface Listener {

        void onSettingsClicked();

        void onFaqClicked();

        void onMyReportClicked();

        void onMyCommentClicked();

        void onScrapClicked();

        void onLoginClicked();

        void onSignOutClicked();
    }

    void bindLoginStatus(boolean hasSignedIn, String email, String displayName);

    void bindProfilePhoto(Uri uri);

    void showSnackBar(String message);

}
