package com.limefriends.molde.common.DI;

import android.app.Activity;

import com.limefriends.molde.common.FromSchemaToEntity;
import com.limefriends.molde.screen.common.bottomNavigationViewHelper.BottomNavigationViewHelper;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.model.repository.usecase.CardNewsUseCase;
import com.limefriends.molde.model.repository.usecase.CommentUseCase;
import com.limefriends.molde.model.repository.usecase.FaqUseCase;
import com.limefriends.molde.model.repository.usecase.FavoriteUseCase;
import com.limefriends.molde.model.repository.usecase.FeedResultUseCase;
import com.limefriends.molde.model.repository.usecase.FeedUseCase;
import com.limefriends.molde.model.repository.usecase.SafehouseUseCase;
import com.limefriends.molde.model.repository.usecase.ScrapUseCase;
import com.limefriends.molde.networking.service.MoldeRestfulService;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.screensNavigator.FragmentScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

public class PresentationCompositionRoot {

    private final CompositionRoot mCompositionRoot;
    private final Activity mActivity;

    public PresentationCompositionRoot(CompositionRoot mCompositionRoot,
                                       Activity mActivity) {
        this.mCompositionRoot = mCompositionRoot;
        this.mActivity = mActivity;
    }

    private Activity getActivity() {
        return mActivity;
    }


    /**
     * RestfulService
     */

    private MoldeRestfulService.Comment getCommentRestfulService() {
        return mCompositionRoot.getCommentRestfulService();
    }

    private MoldeRestfulService.Faq getFaqRestfulService() {
        return mCompositionRoot.getFaqRestfulService();
    }

    private MoldeRestfulService.Favorite getFavoriteRestfulService() {
        return mCompositionRoot.getFavoriteRestfulService();
    }

    private MoldeRestfulService.Feed getFeedRestfulService() {
        return mCompositionRoot.getFeedRestfulService();
    }

    private MoldeRestfulService.FeedResult getFeedResultRestfulService() {
        return mCompositionRoot.getFeedResultRestfulService();
    }

    private MoldeRestfulService.CardNews getCardNewsRestfulService() {
        return mCompositionRoot.getCardNewsRestfulService();
    }

    private MoldeRestfulService.Safehouse getSafehouseRestfulService() {
        return mCompositionRoot.getSafehouseRestfulService();
    }

    private MoldeRestfulService.Scrap getScrapRestfulService() {
        return mCompositionRoot.getScrapRestfulService();
    }


    /**
     * View
     */

    private FromSchemaToEntity getFromSchemaToEntity() {
        return new FromSchemaToEntity();
    }

    public ActivityScreenNavigator getActivityScreenNavigator() {
        return new ActivityScreenNavigator(getActivity());
    }

    public FragmentScreenNavigator getFragmentScreenNavigator() {
        // TODO -
        return null;
    }

    public ToastHelper getToastHelper() {
        return new ToastHelper(getActivity());
    }

    public BottomNavigationViewHelper getBottomNavigationViewHelper() {
        return new BottomNavigationViewHelper();
    }


    /**
     * UseCase
     */

    public Repository.CardNews getCardNewsUseCase() {
        return new CardNewsUseCase(
                getCardNewsRestfulService(),
                getFromSchemaToEntity()
        );
    }

    public Repository.Scrap getScrapUseCase() {
        return new ScrapUseCase(
                getScrapRestfulService(),
                getCardNewsRestfulService(),
                getFromSchemaToEntity()
        );
    }

    public Repository.Feed getFeedUseCase() {
        return new FeedUseCase(
                getFeedRestfulService(),
                getFromSchemaToEntity()
        );
    }

    public Repository.FeedResult getFeedResultUseCase() {
        return new FeedResultUseCase(getFeedResultRestfulService());
    }

    public Repository.Comment getCommentUseCase() {
        return new CommentUseCase(
                getCommentRestfulService(),
                getCardNewsRestfulService(),
                getFromSchemaToEntity());
    }

    public Repository.Faq getFaqUseCase() {
        return new FaqUseCase(
                getFaqRestfulService(),
                getFromSchemaToEntity());
    }

    public Repository.Favorite getFavoriteUseCase() {
        return new FavoriteUseCase(
                getFavoriteRestfulService(),
                getFromSchemaToEntity());
    }

    public Repository.Safehouse getSafehouseUseCase() {
        return new SafehouseUseCase(
                getSafehouseRestfulService(),
                getFromSchemaToEntity());
    }

}
