package com.limefriends.molde.screen.controller.magazine.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseFragment;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.view.magazine.main.CardNewsView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class CardNewsFragment extends BaseFragment implements CardNewsView.Listener {

    public static CardNewsFragment newInstance() {
        CardNewsFragment cardNewsFragment = new CardNewsFragment();
        return cardNewsFragment;
    }


    @Service private Repository.CardNews mCardNewsRepository;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    @Service private ViewFactory mViewFactory;
    @Service private ToastHelper mToastHelper;

    private CardNewsView mCardNewsView;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isFirstOnCreateView = true;
    private boolean isLoading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getInjector().inject(this);

        if (mCardNewsView == null){
            mCardNewsView = mViewFactory.newInstance(CardNewsView.class, container);
        }
        return mCardNewsView.getRootView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mCardNewsView.registerListener(this);

        if (isFirstOnCreateView) loadMagazine(PER_PAGE, FIRST_PAGE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeDisposable.clear();
    }

    private void loadMagazine(int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        List<CardNewsEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mCardNewsRepository
                        .getCardNewsList(perPage, page)
                        .subscribe(
                                data::addAll,
                                err -> isFirstOnCreateView = false,
                                () -> {
                                    isFirstOnCreateView = false;
                                    if (data.size() == 0) {
                                        isLoading = false;
                                        hasMoreToLoad(false);
                                        return;
                                    } else if (data.size() < PER_PAGE) {
                                        hasMoreToLoad(false);
                                    }
                                    mCardNewsView.bindCardNews(data);
                                    currentPage++;
                                    isLoading = false;
                                }
                        )
        );
    }

    private void refreshMagazine() {

        hasMoreToLoad = true;

        currentPage = FIRST_PAGE;

        isLoading = true;

        List<CardNewsEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mCardNewsRepository
                        .getCardNewsList(PER_PAGE, FIRST_PAGE)
                        .subscribe(
                                data::addAll,
                                err -> isFirstOnCreateView = false,
                                () -> {

                                    if (data.size() == 0) {
                                        isLoading = false;
                                        hasMoreToLoad(false);
                                        return;
                                    } else if (data.size() < PER_PAGE) {
                                        hasMoreToLoad(false);
                                    }
                                    mCardNewsView.setRefreshDone();
                                    mCardNewsView.clearCardNews();
                                    mCardNewsView.bindCardNews(data);
                                    currentPage++;
                                    isLoading = false;
                                }
                        )
        );
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void onNewMolcaInfoClicked() {
        mActivityScreenNavigator.toRecentMolcaInfoActivity();
    }

    @Override
    public void onHowToPreventClicked() {
        mActivityScreenNavigator.toHowToDetectActivity();
    }

    @Override
    public void onHowToRespondClicked() {
        mActivityScreenNavigator.toHowToRespondActivity();
    }

    @Override
    public void onLoadMore() {
        if (isLoading) return;
        loadMagazine(PER_PAGE, currentPage);
    }

    @Override
    public void onCardNewsClicked(int cardNewsId) {
        mActivityScreenNavigator.toCardNewsDetailActivity(getContext(), cardNewsId);
    }

    @Override
    public void onSwipeToRefreshPulled() {
        refreshMagazine();
    }
}
