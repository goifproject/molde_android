package com.limefriends.molde.screen.view.mypage.report;

import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface MyFeedView extends ObservableView<MyFeedView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onLoadMore();

        void onMyFeedSelected(int position);
    }

    void bindMyFeed(List<FeedEntity> entityList);

    void deleteFeed(int position);

    void updateFeed(int position, FeedEntity entity);

}
