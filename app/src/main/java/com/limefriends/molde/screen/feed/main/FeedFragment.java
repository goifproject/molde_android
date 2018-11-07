package com.limefriends.molde.screen.feed.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseFragment;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.feed.main.view.FeedView;
import com.limefriends.molde.screen.main.MoldeMainActivity;
import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.limefriends.molde.common.Constant.Feed.*;

public class FeedFragment extends BaseFragment implements FeedView.Listener {

    private boolean isLoading;

    @Service private Repository.Feed mFeedRepository;
    @Service private ToastHelper mToastHelper;
    @Service private ViewFactory mViewFactory;

    private FeedView mFeedView;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final int PER_PAGE = 10;
    private final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;

    private boolean isByDistanceFirstCall = true;
    private boolean isByDateFirstCall = true;

    private boolean isFirstCall = true;
    private boolean isSecondCall = false;
    private String feedStandard = FEED_BY_DISTANCE;

    List<FeedEntity> currentlyShownData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getInjector().inject(this);

        if (mFeedView == null) {
            mFeedView = mViewFactory.newInstance(FeedView.class, container);
        }
        return mFeedView.getRootView();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFeedView.registerListener(this);
        mFeedView.setToggle(feedStandard);
        if (isByDistanceFirstCall) loadFeedData(feedStandard, PER_PAGE, currentPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeDisposable.clear();
        mFeedView.unregisterListener(this);
    }

    private void loadFeedData(String feedStandard, int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        if (feedStandard.equals(FEED_BY_DISTANCE)) {
            this.feedStandard = FEED_BY_DISTANCE;
            fetchByLocation(perPage, page);
        } else if (feedStandard.equals(FEED_BY_LAST)) {
            this.feedStandard = FEED_BY_LAST;
            fetchByDate(perPage, page);
        }
    }

    private void fetchByLocation(int perPage, int page) {

        LatLng latLng = ((MoldeApplication) getActivity().getApplication()).getCurrLocation();

        mCompositeDisposable.add(
                mFeedRepository
                        .getPagedFeedByDistance(latLng.latitude, latLng.longitude, perPage, page)
                        .subscribeWith(getObserver())
        );
    }

    private void fetchByDate(int perPage, int page) {

        mCompositeDisposable.add(
                mFeedRepository
                        .getPagedFeedByDate(perPage, page)
                        .subscribeWith(getObserver())
        );
    }

    private DisposableObserver<List<FeedEntity>> getObserver() {

        List<FeedEntity> unfilteredData = new ArrayList<>();
        List<FeedEntity> filteredData = new ArrayList<>();
        return new DisposableObserver<List<FeedEntity>>() {
            @Override
            public void onNext(List<FeedEntity> feedEntities) {
                for (FeedEntity entity : feedEntities) {
                    if (entity.getRepState() == Constant.ReportState.RECEIVING) continue;
                    if (entity.getRepState() == Constant.ReportState.DENIED) continue;
                    filteredData.add(entity);
                }
                unfilteredData.addAll(feedEntities);
                currentlyShownData.addAll(filteredData);
            }

            @Override
            public void onError(Throwable e) {
                isLoading = false;
            }

            @Override
            public void onComplete() {

                isLoading = false;
                isByDistanceFirstCall = false;

                if (unfilteredData.size() == 0) {
                    hasMoreToLoad(false);
                    return;
                }

                mFeedView.bindFeed(filteredData);
                currentPage++;

                if (unfilteredData.size() < PER_PAGE) {
                    hasMoreToLoad(false);
                }
            }
        };
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onToggleChanged(boolean isChecked) {
        // 오른쪽, 시간순
        if (isChecked) {

             if (!isByDateFirstCall) return;

            isByDateFirstCall = false;

            hasMoreToLoad(true);

            currentPage = FIRST_PAGE;

            mFeedView.clearFeed();
            currentlyShownData.clear();

            loadFeedData(FEED_BY_LAST, PER_PAGE, currentPage);
        }
        // 왼쪽, 거리순
        else {

            isByDateFirstCall = true;

            hasMoreToLoad(true);

            currentPage = FIRST_PAGE;

            mFeedView.clearFeed();
            currentlyShownData.clear();

            loadFeedData(FEED_BY_DISTANCE, PER_PAGE, currentPage);
        }
    }

    @Override
    public void onLoadMore() {
        if (isLoading) return;
        loadFeedData(feedStandard, PER_PAGE, currentPage);
    }

    @Override
    public void onItemClicked(int itemId) {
        for (FeedEntity entity : currentlyShownData) {
            if (entity.getRepId() == itemId) {
                ((MoldeMainActivity) getActivity()).setFeedEntity(entity);
                ((MoldeMainActivity) getActivity()).setSelectedMenu(R.id.main_menu_map);
            }
        }
    }
}
