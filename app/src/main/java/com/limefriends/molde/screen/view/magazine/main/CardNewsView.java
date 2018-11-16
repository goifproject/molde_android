package com.limefriends.molde.screen.view.magazine.main;

import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface CardNewsView extends ObservableView<CardNewsView.Listener> {

    interface Listener {

        void onNewMolcaInfoClicked();

        void onHowToPreventClicked();

        void onHowToRespondClicked();

        void onLoadMore();

        void onCardNewsClicked(int cardNewsId);

        void onSwipeToRefreshPulled();
    }

    void clearCardNews();

    void bindCardNews(List<CardNewsEntity> cardNewsEntities);

    void setRefreshDone();
}
