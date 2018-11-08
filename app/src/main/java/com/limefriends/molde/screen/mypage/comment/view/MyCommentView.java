package com.limefriends.molde.screen.mypage.comment.view;

import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface MyCommentView extends ObservableView<MyCommentView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onMyCommentLoadMore();

        void onReportedCommentLoadMore();

        void onDeleteCommentClicked(int position);

        void onRefuseReportClicked(int position);

        void onReportedCommentClicked(int position);

        void onParentItemClick(int groupPosition, int newsId);

        void onChildItemClick(int childPosition, int newsId, String description);
    }

    void bindComments(List<CardNewsEntity> myComments);

    void bindReportedComments(List<CommentEntity> reportedComments);

    void showProgressIndication();

    void hideProgressIndication();

    void expandMyCommentList(int count);

    void expandGroup(int groupPosition);

    void showSnackBar(String message);

    void deleteComment(int position);
}
