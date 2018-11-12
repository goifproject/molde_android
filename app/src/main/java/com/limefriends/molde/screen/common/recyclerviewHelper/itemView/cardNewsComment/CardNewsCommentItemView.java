package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNewsComment;

import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface CardNewsCommentItemView extends ObservableView<CardNewsCommentItemView.Listener> {

    public interface Listener {

        void onItemClicked(int itemId);
    }

    void bindComment(CommentEntity commentEntity);

}
