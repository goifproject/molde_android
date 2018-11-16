package com.limefriends.molde.screen.view.feed.main;

import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface FeedView extends ObservableView<FeedView.Listener> {

    public interface Listener {

        void onToggleChanged(boolean isChecked);

        void onLoadMore();

        void onItemClicked(int itemId);

        void onSwipeToRefreshPulled();
    }

    void setToggle(String standard);

    void bindFeed(List<FeedEntity> feedList);

    void bindLastData(String date);

    void clearFeed();

    void setRefreshDone();



}
