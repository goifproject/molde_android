package com.limefriends.molde.screen.view.map.search;

import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface SearchLocationView extends ObservableView<SearchLocationView.Listener> {

    interface Listener {

        void onSearchLocationClicked(String keyword);

        void onLocationItemClicked(int position);

        void onDeleteHistoryClicked();

        void onDeleteHistoryClicked(int position);
    }

    void bindSearchInfo(List<SearchInfoEntity> entities);

    void deleteHistoryItem(int position);

    void clearHistory();
}
