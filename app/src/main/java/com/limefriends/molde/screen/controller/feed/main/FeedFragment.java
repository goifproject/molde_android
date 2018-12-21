package com.limefriends.molde.screen.controller.feed.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseFragment;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.view.feed.main.FeedView;
import com.limefriends.molde.screen.controller.main.MoldeMainActivity;
import com.limefriends.molde.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.limefriends.molde.common.Constant.Feed.*;

public class FeedFragment extends BaseFragment implements FeedView.Listener {

    public static FeedFragment newInstance() {
        FeedFragment feedFragment = new FeedFragment();
        return feedFragment;
    }

    private boolean isLoading;
    private boolean hasOnRefreshCalled;

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

    private void refreshFeedData(String feedStandard) {

        currentlyShownData.clear();

        hasMoreToLoad = true;

        currentPage = FIRST_PAGE;

        isLoading = true;

        if (feedStandard.equals(FEED_BY_DISTANCE)) {
            this.feedStandard = FEED_BY_DISTANCE;
            fetchByLocation(PER_PAGE, FIRST_PAGE);
        } else if (feedStandard.equals(FEED_BY_LAST)) {
            this.feedStandard = FEED_BY_LAST;
            fetchByDate(PER_PAGE, FIRST_PAGE);
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

                if (hasOnRefreshCalled) {
                    mFeedView.clearFeed();
                    mFeedView.setRefreshDone();
                    hasOnRefreshCalled = false;
                }

                // 아예 데이터가 없거나 불러오는데 오류가 생긴 경우
                if (unfilteredData.size() == 0) {
                    hasMoreToLoad(false);
                    return;
                }

                if (filteredData.size() != 0) {
                    mFeedView.bindLastData(DateUtil.fromLongToDate(currentlyShownData.get(0).getRepDate()));
                } else {
                    mToastHelper.showShortToast("신고된 피드를 처리중입니다.");
                }

                mFeedView.bindFeed(filteredData);
                currentPage++;

                // 불러온 개수가 10개 미만일 경우 더 이상 호출하지 않는다
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
                ((MoldeMainActivity) getActivity()).changeTab(R.id.main_menu_map);
            }
        }
    }

    @Override
    public void onSwipeToRefreshPulled() {
        if (isLoading) return;
        hasOnRefreshCalled = true;
        refreshFeedData(feedStandard);
    }
}
