package com.limefriends.molde.screen.magazine.main.view;

import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.screen.common.views.ObservableView;

public interface CardNewsItemView extends ObservableView<CardNewsItemView.Listener>{

    interface Listener {
        void onCardNewsClicked(int cardNewsId);
    }

    void bindCardNews(CardNewsEntity cardNews);

}

