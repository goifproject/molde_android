package com.limefriends.molde.screen.magazine.main.view;

import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.screen.common.views.ObservableView;

import java.util.List;

public interface CardNewsView extends ObservableView<CardNewsView.Listener> {

    interface Listener {

        void onNewMolcaInfoClicked();

        void onHowToPreventClicked();

        void onHowToRespondClicked();

        void onLoadMore();

        void onCardNewsClicked(int cardNewsId);
    }

    void bindCardNews(List<CardNewsEntity> cardNewsEntities);
}
