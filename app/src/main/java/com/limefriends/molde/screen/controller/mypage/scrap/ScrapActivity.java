package com.limefriends.molde.screen.controller.mypage.scrap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.view.mypage.scrap.ScrapView;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.disposables.CompositeDisposable;

import static com.limefriends.molde.common.Constant.Scrap.*;

public class ScrapActivity extends BaseActivity implements ScrapView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent(context, ScrapActivity.class);
        context.startActivity(intent);
    }

    @Service private Repository.Scrap mScrapUseCase;
    @Service private ToastHelper mToastHelper;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    @Service private ViewFactory mViewFactory;
    private ScrapView mScrapView;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private List<CardNewsEntity> currentlyShownList = new ArrayList<>();
    private int selectedNewsId;

    private final int PER_PAGE = 10;
    private final int FIRST_PAGE = 0;
    private int currentPage = FIRST_PAGE;

    private boolean hasMoreToLoad = true;
    private boolean isLoading;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mScrapView = mViewFactory.newInstance(ScrapView.class, null);

        setContentView(mScrapView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mScrapView.registerListener(this);
        if (isFirst) loadMyScrap(PER_PAGE, currentPage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
        hasMoreToLoad(true);
        currentPage = 0;
    }

    private void loadMyScrap(int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        mScrapView.showProgressIndication();

        String uId = ((MoldeApplication) getApplication()).getFireBaseAuth().getCurrentUser().getUid();

        List<CardNewsEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mScrapUseCase
                        .getScrapList(uId, perPage, page)
                        .subscribe(
                                cardNewsEntity -> data.add(cardNewsEntity),
                                err -> mToastHelper.showShortToast("스크랩을 불러오는데 실패했습니다"),
                                () -> {
                                    if (data.size() == 0) {
                                        hasMoreToLoad(false);
                                        isLoading = false;
                                        mScrapView.hideProgressIndication();
                                        return;
                                    }
                                    mScrapView.hideProgressIndication();
                                    mScrapView.bindScrap(data);
                                    currentlyShownList.addAll(data);
                                    currentPage++;
                                    isLoading = false;
                                    isFirst = false;
                                    if (data.size() < PER_PAGE) {
                                        hasMoreToLoad(false);
                                    }
                                }

                        )
        );
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INTENT_KEY_CARDNEWS_DETAIL) {
            for (CardNewsEntity entity : currentlyShownList) {
                if (entity.getNewsId() == selectedNewsId) {
                    mScrapView.deleteScrap(entity);
                    break;
                }
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
        loadMyScrap(PER_PAGE, currentPage);
    }

    @Override
    public void onScrapClicked(int cardNewsId) {
        selectedNewsId = cardNewsId;
        mActivityScreenNavigator
                .toCardNewsDetailActivity(this, cardNewsId, INTENT_KEY_CARDNEWS_DETAIL);
    }
}