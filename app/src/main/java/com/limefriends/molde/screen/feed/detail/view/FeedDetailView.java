package com.limefriends.molde.screen.feed.detail.view;

import android.net.Uri;
import android.support.v4.util.SparseArrayCompat;

import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.views.ObservableView;

public interface FeedDetailView extends ObservableView<FeedDetailView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onDeleteClicked(int feedId);

        void onRefuseClicked(int feedId);

        void onUpdateClicked(int feedId, int state);

        void onSelectPictureClicked(int sequence, int size);

        void onReportInspectionClicked(SparseArrayCompat<Uri> images);
    }

    void switchToAdminPage();

    void showSnackBar(String message);

    void setPictureForReport(Uri uri, int seq);

    void bindFeedDetail(FeedEntity entity);

    void changeAdminProgress(int state);

    void hideImageList();

}
