package com.limefriends.molde.screen.view.magazine.detail;


import com.limefriends.molde.model.entity.cardNews.CardNewsImageEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface CardNewsDetailView extends ObservableView<CardNewsDetailView.Listener> {

    /**
     * Observer Callback Method
     */

    public interface Listener {

        void onCommentIconClicked(String description);

        void onScrapClicked();

        void onShareClicked();

        void onDeleteScrapClicked();
    }

    /**
     * change view indication
     */

    void showPromptDialog();

    void showSnackBar(String message);

    void showProgressIndication();

    void hideProgressIndication();

    /**
     * bind data
     */

    void bindCardNewsDescription(String description);

    void bindCardNewsImages(List<CardNewsImageEntity> images);

    void setIsScrap();

    void unsetScrap();

    void bindPageCount(int count);

}
