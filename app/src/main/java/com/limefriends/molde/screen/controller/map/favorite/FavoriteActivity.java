package com.limefriends.molde.screen.controller.map.favorite;

import android.content.Intent;
import android.os.Bundle;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.view.map.favorite.FavoriteView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class FavoriteActivity extends BaseActivity implements FavoriteView.Listener {

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;

    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;
    private boolean isLoading;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Service private Repository.Favorite mFavoriteRepository;
    @Service private ToastHelper mToastHelper;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;

    @Service private ViewFactory mViewFactory;
    private FavoriteView mFavoriteView;
    List<FavoriteEntity> favoriteEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mFavoriteView = mViewFactory.newInstance(FavoriteView.class, null);

        setContentView(mFavoriteView.getRootView());

    }

    @Override
    protected void onStart() {
        super.onStart();

        mFavoriteView.registerListener(this);

        loadFavorite(PER_PAGE, currentPage);
    }

    private void loadFavorite(int perPage, int page) {

        if (!hasMoreToLoad) return;

        isLoading = true;

        String uId
                = ((MoldeApplication)getApplication()).getFireBaseAuth().getCurrentUser().getUid();

        List<FavoriteEntity> data = new ArrayList<>();
        mCompositeDisposable.add(
                mFavoriteRepository
                        .getMyFavorite(uId, perPage, page)
                        .subscribe(
                                data::addAll,
                                err -> {},
                                () -> {
                                    isLoading = false;

                                    if (data.size() == 0) {
                                        setHasMoreToLoad(false);
                                        return;
                                    }
                                    favoriteEntities.addAll(data);
                                    mFavoriteView.bindFavorites(data);
                                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                                    currentPage++;

                                    if (data.size() < PER_PAGE) {
                                        setHasMoreToLoad(false);
                                    }
                                }
                        )
        );
    }

    private void deleteFavorite(int favId) {

        String uId =  ((MoldeApplication)getApplication()).getFireBaseAuth().getUid();

        mCompositeDisposable.add(
                mFavoriteRepository
                        .deleteFavorite(uId, favId)
                        .subscribe(
                                e -> {
                                    for (FavoriteEntity entity : favoriteEntities) {
                                        if (entity.getFavId() == favId) {
                                            mFavoriteView.removeFavorite(entity);
                                        }
                                    }
                                    mToastHelper.showShortToast("즐겨찾기가 삭제되었습니다.");
                                },
                                err -> {},
                                () -> {}
                        )
        );
    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }

    @Override
    public void onNavigateUpClicked() {
        onBackPressed();
    }

    @Override
    public void onLoadMore() {
        if (isLoading) return;
        loadFavorite(PER_PAGE, currentPage);
    }

    @Override
    public void onFavoriteClicked(int favId) {
        for (FavoriteEntity entity : favoriteEntities) {
            if (entity.getFavId() == favId) {
                Intent intent = new Intent();
                intent.putExtra("mapFavoriteInfo", entity);
                setResult(RESULT_OK, intent);
                finish();
                break;
            }
        }
    }

    @Override
    public void onFavoriteDeleteClicked(int favId) {
        deleteFavorite(favId);
    }
}

