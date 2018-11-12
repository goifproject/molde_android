package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportedComment;

import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface ReportedCommentItemView extends ObservableView<ReportedCommentItemView.Listener> {

    public interface Listener {

        void onItemClicked(int position);

        void onItem2Clicked(int position);
    }

    void bindComment(CommentEntity entity, int position);

}
