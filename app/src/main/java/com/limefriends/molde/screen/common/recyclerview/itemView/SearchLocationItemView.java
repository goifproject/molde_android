package com.limefriends.molde.screen.common.recyclerview.itemView;

import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface SearchLocationItemView extends ObservableView<SearchLocationItemView.Listener> {

    public interface Listener {

        void onItemClicked(int itemId);

        void onItem2Clicked(int position);
    }

    void bindSearchInfo(SearchInfoEntity entity, int position);
}
