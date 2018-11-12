package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportCard;

import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface ReportCardItemView extends ObservableView<ReportCardItemView.Listener> {

    interface Listener {

        void onItemClicked(int feedId);
    }

    void bindFeed(FeedEntity entity);
}
