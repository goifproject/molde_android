package com.limefriends.molde.screen.mypage.scrap.view;

import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;
import java.util.Observable;

public interface ScrapView extends ObservableView<ScrapView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onLoadMore();

        void onScrapClicked(int cardNewsId);
    }

    void showProgressIndication();

    void hideProgressIndication();

    void bindScrap(List<CardNewsEntity> scrapList);

    void deleteScrap(CardNewsEntity entity);

}
