package com.limefriends.molde.screen.map.favorite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.TextView;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.MoldeApplication;
import com.limefriends.molde.screen.common.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.common.utils.NetworkUtil;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class MapFavoriteActivity extends BaseActivity
        implements MapFavoriteAdapter.MyFavoriteAdapterCallBack {

    private static final int PER_PAGE = 10;
    private static final int FIRST_PAGE = 0;
    public static final String DELETE_FAVORITE_DIALOG = "DELETE_FAVORITE_DIALOG";
    private int currentPage = FIRST_PAGE;
    private boolean hasMoreToLoad = true;

    @BindView(R.id.my_favorite_list_view)
    AddOnScrollRecyclerView my_favorite_list_view;

    private MapFavoriteAdapter myFavoriteAdapter;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Service private Repository.Favorite mFavoriteRepository;
    @Service private ToastHelper mToastHelper;
    @Service private DialogFactory mDialogFactory;
    @Service private DialogManager mDialogManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        setContentView(R.layout.activity_map_favorite);

        setupViews();

        setupFavoriteList();

        loadFavorite(PER_PAGE, currentPage);
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
        toolbar_title.setText(getText(R.string.my_favorite));
    }

    private void setupFavoriteList() {
        myFavoriteAdapter = new MapFavoriteAdapter(getApplicationContext());
        myFavoriteAdapter.setMoldeMyFavoriteAdapterCallBack(this);
        my_favorite_list_view.setAdapter(myFavoriteAdapter);
        my_favorite_list_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()), false);
        my_favorite_list_view.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                loadFavorite(PER_PAGE, currentPage);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void showDeleteDialog(final int favId) {

        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getText(R.string.dialog_delete_favorite_message).toString(),
                "",
                getText(R.string.yes).toString(),
                getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                onUnSelected(favId);
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, DELETE_FAVORITE_DIALOG);
    }

    //-----
    // Network
    //-----


    private void loadFavorite(int perPage, int page) {

        if (!NetworkUtil.isConnected(this)) {
            mToastHelper.showNetworkError();
            return;
        }

        if (!hasMoreToLoad) return;

        my_favorite_list_view.setIsLoading(true);

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
                                    my_favorite_list_view.setIsLoading(false);

                                    if (data.size() == 0) {
                                        setHasMoreToLoad(false);
                                        return;
                                    }

                                    myFavoriteAdapter.setData(data);
                                    // 7. 추가 완료 후 다음 페이지로 넘어가도록 세팅
                                    currentPage++;

                                    if (data.size() < PER_PAGE) {
                                        setHasMoreToLoad(false);
                                    }
                                }
                        )
        );
    }

    private void deleteFavorite(final int favId) {

        if (!NetworkUtil.isConnected(this)) {
            mToastHelper.showNetworkError();
            return;
        }

        String uId =  ((MoldeApplication)getApplication()).getFireBaseAuth().getUid();

        mCompositeDisposable.add(
                mFavoriteRepository
                        .deleteFavorite(uId, favId)
                        .subscribe(
                                e -> {},
                                err -> {},
                                () -> {
                                    mToastHelper.showShortToast("즐겨찾기가 삭제되었습니다.");
                                    myFavoriteAdapter.notifyFavoriteRemoved();
                                }
                        )
        );
    }

    private void setHasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    @Override
    public void applyMyFavoriteMapInfo(FavoriteEntity entity) {
        Intent intent = new Intent();
        intent.putExtra("mapFavoriteInfo", entity);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onUnSelected(int favId) {
        deleteFavorite(favId);
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroy() {
        super.onDestroy();
        setHasMoreToLoad(true);
        currentPage = 0;
    }

}
