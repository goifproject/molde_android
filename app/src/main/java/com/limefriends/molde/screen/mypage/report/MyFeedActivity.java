package com.limefriends.molde.screen.mypage.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.R;
import com.limefriends.molde.common.Constant;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.screen.common.recyclerview.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.feed.detail.FeedDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

public class MyFeedActivity extends BaseActivity implements MyFeedAdapter.OnItemClickListener {

    @BindView(R.id.myReport_recyclerView)
    AddOnScrollRecyclerView myReport_recyclerView;

    private MyFeedAdapter reportAdapter;

    @Service
    private Repository.Feed mFeedUseCase;
    @Service
    private ToastHelper mToastHelper;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

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
        setContentView(R.layout.activity_my_feed);

        getInjector().inject(this);

        authority = PreferenceUtil.getLong(this, PREF_KEY_AUTHORITY, Constant.Authority.GUEST);

        userId = FirebaseAuth.getInstance().getUid();

        setupViews();

        setFeedRecycler();

        loadMyReport(PER_PAGE, currentPage);
    }

    //-----
    // View
    //-----

    private void setupViews() {
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        toolbar_title.setText(getText(R.string.myreport));
    }

    private void setFeedRecycler() {
        reportAdapter = new MyFeedAdapter(this, this);
        myReport_recyclerView.setAdapter(reportAdapter);
        myReport_recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()), false);
        myReport_recyclerView.setOnLoadMoreListener(() -> {
            if (isLoading) return;
            loadMyReport(PER_PAGE, currentPage);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    //-----
    // Network
    //-----

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
                                    reportAdapter.addData(entities);
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
    public void OnItemClick(int feedId, int position) {
        Intent intent = new Intent(this, FeedDetailActivity.class);
        intent.putExtra(EXTRA_KEY_FEED_ID, feedId);
        intent.putExtra(EXTRA_KEY_ACTIVITY_NAME, INTENT_VALUE_MY_FEED);
        intent.putExtra(EXTRA_KEY_POSITION, position);
        startActivityForResult(intent, INTENT_KEY_MY_FEED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INTENT_KEY_MY_FEED) {

            int state = data.getIntExtra(EXTRA_KEY_STATE, -1);
            int position = data.getIntExtra(EXTRA_KEY_POSITION, 0);

            if (state == -1) {
                reportAdapter.removeItem(position);
            } else {
                reportAdapter.updateItem(position, state);
            }
        }
    }

    //-----
    // lifecycle
    //-----

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }
}