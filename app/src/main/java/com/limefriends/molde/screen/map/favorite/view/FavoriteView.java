package com.limefriends.molde.screen.map.favorite.view;

import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.views.ObservableView;

import java.util.List;

public interface FavoriteView extends ObservableView<FavoriteView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onLoadMore();

        void onFavoriteClicked(int favId);

        void onFavoriteDeleteClicked(int favId);
    }

    void bindFavorites(List<FavoriteEntity> entityList);

    void removeFavorite(FavoriteEntity favoriteEntity);

}
