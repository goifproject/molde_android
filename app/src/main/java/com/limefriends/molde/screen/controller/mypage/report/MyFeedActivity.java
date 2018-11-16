package com.limefriends.molde.screen.controller.mypage.report;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.controller.feed.detail.FeedDetailActivity;
import com.limefriends.molde.screen.view.mypage.report.MyFeedView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

import static com.limefriends.molde.common.Constant.Authority.*;
import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_ACTIVITY_NAME;
import static com.limefriends.molde.common.Constant.Common.EXTRA_KEY_POSITION;
import static com.limefriends.molde.common.Constant.Common.PREF_KEY_AUTHORITY;
import static com.limefriends.molde.common.Constant.Feed.EXTRA_KEY_FEED_ID;
import static com.limefriends.molde.common.Constant.Feed.EXTRA_KEY_STATE;
import static com.limefriends.molde.common.Constant.Feed.INTENT_KEY_MY_FEED;
import static com.limefriends.molde.common.Constant.Feed.INTENT_VALUE_MY_FEED;

public class MyFeedActivity
        extends BaseActivity implements MyFeedView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent(context, MyFeedActivity.class);
        context.startActivity(intent);
    }

    @Service private Repository.Feed mFeedUseCase;
    @Service private ToastHelper mToastHelper;
    @Service private ViewFactory mViewFactory;
    private MyFeedView mMyFeedView;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private List<FeedEntity> currentlyShownMyFeed = new ArrayList<>();

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private long authority;
    private String userId;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mMyFeedView = mViewFactory.newInstance(MyFeedView.class, null);

        setContentView(mMyFeedView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMyFeedView.registerListener(this);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);

        userId = FirebaseAuth.getInstance().getUid();

        loadMyReport(PER_PAGE, currentPage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }


    private Observable<List<FeedEntity>> getFeedObservable(String userId, int perPage, int page) {

        if (authority == MEMBER || authority == GUARDIAN) {
            return mFeedUseCase.getMyFeed(userId, perPage, page);
        } else if (authority == ADMIN) {
            return mFeedUseCase.getPagedFeedByDate(perPage, page);
        }
        return Observable.empty();
    }

    private void loadMyReport(int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        List<FeedEntity> entities = new ArrayList<>();
        mCompositeDisposable.add(
                getFeedObservable(userId, perPage, page)
                        .subscribe(
                                entities::addAll,
                                e -> {},
                                () -> {
                                    isLoading = false;

                                    if (entities.size() == 0) {
                                        setHasMoreToLoad(false);
                                        return;
                                    }
                                    mMyFeedView.bindMyFeed(entities);
                                    currentlyShownMyFeed.addAll(entities);
                                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                                    currentPage++;
                                    if (entities.size() < PER_PAGE) {
                                        setHasMoreToLoad(false);
                                    }
                                })
        );
    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INTENT_KEY_MY_FEED) {

            int state = data.getIntExtra(EXTRA_KEY_STATE, -1);
            int position = data.getIntExtra(EXTRA_KEY_POSITION, 0);

            if (state == -1) {
                mMyFeedView.deleteFeed(position);
            } else {
                FeedEntity entity = currentlyShownMyFeed.get(position);
                entity.setRepState(state);
                mMyFeedView.updateFeed(position, entity);
            }
        }
    }

    @Override
    public void onNavigateUpClicked() {
        onBackPressed();
    }

    @Override
    public void onLoadMore() {
        if (isLoading) return;
        loadMyReport(PER_PAGE, currentPage);
    }

    @Override
    public void onMyFeedSelected(int position) {
        Intent intent = new Intent(this, FeedDetailActivity.class);
        intent.putExtra(EXTRA_KEY_FEED_ID, currentlyShownMyFeed.get(position).getRepId());
        intent.putExtra(EXTRA_KEY_ACTIVITY_NAME, INTENT_VALUE_MY_FEED);
        intent.putExtra(EXTRA_KEY_POSITION, position);
        startActivityForResult(intent, INTENT_KEY_MY_FEED);
    }
}