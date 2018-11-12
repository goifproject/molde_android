package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.feed;

import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface FeedItemView extends ObservableView<FeedItemView.Listener> {

    public interface Listener {

        void onItemClicked(int position);
    }

    void bindFeed(FeedEntity entity);

}
