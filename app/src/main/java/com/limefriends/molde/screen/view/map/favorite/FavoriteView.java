package com.limefriends.molde.screen.view.map.favorite;

import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

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
