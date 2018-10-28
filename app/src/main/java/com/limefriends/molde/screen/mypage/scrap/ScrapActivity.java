package com.limefriends.molde.screen.mypage.scrap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.screen.common.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.limefriends.molde.common.Constant.Scrap.*;

public class ScrapActivity extends BaseActivity {

    @BindView(R.id.myScrap_recyclerView)
    AddOnScrollRecyclerView myScrap_recyclerView;
    @BindView(R.id.progressBar3)
    ProgressBar progressBar;

    @Service private Repository.Scrap mScrapUseCase;
    @Service private ToastHelper mToastHelper;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;

    private ScrapAdapter adapter;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private final int PER_PAGE = 10;
    private final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;

    private boolean hasMoreToLoad = true;
    private int selectedNewsPosition;
    private boolean isLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        setContentView(R.layout.activity_my_scrap);

        setupViews();

        setupScrapList();

        loadMyScrap(PER_PAGE, currentPage);
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
        toolbar_title.setText(getText(R.string.myscrap));
    }

    private void setupScrapList() {
        adapter = new ScrapAdapter(this);
        myScrap_recyclerView.setAdapter(adapter);
        myScrap_recyclerView.setLayoutManager(
                new GridLayoutManager(this, 2), true);
        myScrap_recyclerView.setOnLoadMoreListener(() -> {
            if (isLoading) return;
            loadMyScrap(PER_PAGE, currentPage);
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

    private void loadMyScrap(int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        String uId = ((MoldeApplication) getApplication()).getFireBaseAuth().getCurrentUser().getUid();

        mCompositeDisposable.add(
                mScrapUseCase
                        .getScrapList(uId, perPage, page)
                        .subscribeWith(getDisposable())
        );
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    public void onCardNewsSelected(int newsId, int position) {
        selectedNewsPosition = position;
        mActivityScreenNavigator
                .toCardNewsDetailActivity(this, newsId, INTENT_KEY_CARDNEWS_DETAIL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INTENT_KEY_CARDNEWS_DETAIL) {
            adapter.removeItem(selectedNewsPosition);
        }
    }

    private DisposableObserver<CardNewsEntity> getDisposable() {
        List<CardNewsEntity> data = new ArrayList<>();
        return new DisposableObserver<CardNewsEntity>() {
            @Override
            public void onNext(CardNewsEntity cardNewsEntity) {
                data.add(cardNewsEntity);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("에러", e.toString());
            }

            @Override
            public void onComplete() {
                if (data.size() == 0) {
                    hasMoreToLoad(false);
                    isLoading = false;
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                progressBar.setVisibility(View.GONE);
                adapter.addData(data);
                currentPage++;
                isLoading = false;
                if (data.size() < PER_PAGE) {
                    hasMoreToLoad(false);
                }
            }
        };
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
        hasMoreToLoad(true);
        currentPage = 0;
    }

}