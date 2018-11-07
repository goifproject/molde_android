package com.limefriends.molde.screen.feed.main.view;

import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface FeedView extends ObservableView<FeedView.Listener> {

    public interface Listener {

        void onToggleChanged(boolean isChecked);

        void onLoadMore();

        void onItemClicked(int itemId);
    }

    void setToggle(String standard);

    void bindFeed(List<FeedEntity> feedList);

    void clearFeed();

}
