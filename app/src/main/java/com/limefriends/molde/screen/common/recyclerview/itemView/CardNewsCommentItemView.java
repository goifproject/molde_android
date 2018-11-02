package com.limefriends.molde.screen.common.recyclerview.itemView;

import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.views.ObservableView;

public interface CardNewsCommentItemView extends ObservableView<CardNewsCommentItemView.Listener> {

    public interface Listener {

        void onItemClicked(int itemId);
    }

    void bindComment(CommentEntity commentEntity);

}
