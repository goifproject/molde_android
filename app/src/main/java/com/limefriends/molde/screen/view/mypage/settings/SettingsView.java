package com.limefriends.molde.screen.view.mypage.settings;

import com.limefriends.molde.screen.common.view.ObservableView;

public interface SettingsView extends ObservableView<SettingsView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onFavoritePushCheckChanged(boolean isChecked);

        void onFeedChangePushCheckChanged(boolean isChecked);
    }

    void showSnackBar(String message);

    void bindFavoritePush(boolean isChecked);

    void bindFeedPush(boolean isChecked);

}
