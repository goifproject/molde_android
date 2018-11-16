package com.limefriends.molde.screen.view.magazine.comment;

import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface CardNewsCommentView extends ObservableView<CardNewsCommentView.Listener> {

    interface Listener {

        void onNavigateUpClicked();

        void onReportCommentClicked(int commentId);

        void onSendCommentClicked(String comment);

        void onLoadMore();
    }

    void showSnackBar(String message);

    void bindComment(CommentEntity entity);

    void bindComments(List<CommentEntity> entityList);
}
