package com.limefriends.molde.screen.common.recyclerview.itemView.favorite;

import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface FavoriteItemView extends ObservableView<FavoriteItemView.Listener> {

    public interface Listener {

        // Bundle로 처리해야 함
        void onItemClicked(int itemId);

        // void onDeleteFavoriteClicked(int favId);
        void onItem2Clicked(int itemId);
    }

    void bindFavorite(FavoriteEntity entity);

}
