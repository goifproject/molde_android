package com.limefriends.molde.screen.magazine.main.view;

import com.limefriends.molde.screen.common.views.ObservableView;

public interface CardNewsView extends ObservableView<CardNewsView.Listener> {

    public interface Listener {

        void onNewMolcaInfoClicked();

        void onHowToPreventClicked();

        void onHowToRespondClicked();

        void onLoadMore();
    }



}
