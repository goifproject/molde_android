package com.limefriends.molde.screen.common.recyclerview.itemView.myFeed;

import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface MyFeedItemView extends ObservableView<MyFeedItemView.Listener> {

    public interface Listener {

        void onItemClicked(int position);
    }

    void bindFeed(FeedEntity entity, int position);

}
