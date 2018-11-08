package com.limefriends.molde.screen.map.search;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.model.entity.search.SearchInfoEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.map.search.view.SearchLocationView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class SearchLocationActivity extends BaseActivity implements SearchLocationView.Listener {

    @Service private Repository.SearchLocation mSearchLocationRepository;
    @Service private ViewFactory mViewFactory;
    @Service private ToastHelper mToastHelper;
    private SearchLocationView mSearchLocationView;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private List<SearchInfoEntity> currentlyShownSearchInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mSearchLocationView = mViewFactory.newInstance(SearchLocationView.class, null);

        setContentView(mSearchLocationView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSearchLocationView.registerListener(this);

        loadSearchHistory();
    }

    private void loadSearchHistory() {

        mCompositeDisposable.add(
                mSearchLocationRepository
                        .getSearchHistory()
                        .subscribe(
                                searchInfoEntities -> {
                                    currentlyShownSearchInfo = searchInfoEntities;
                                    mSearchLocationView.bindSearchInfo(searchInfoEntities);
                                },
                                err -> {},
                                () -> {}
                        )
        );
    }

    private void addToSearchHistory(SearchInfoEntity entity) {

        mCompositeDisposable.add(
                mSearchLocationRepository
                        .addToSearchHistory(entity)
                        .subscribe(
                                e -> {},
                                err -> Log.e("호출확인", err.getMessage()),
                                () -> {}
                        )
        );
    }

    @Override
    public void onSearchLocationClicked(String keyword) {

        mCompositeDisposable.add(
                mSearchLocationRepository
                        .getTMapSearchAddressInfo(keyword)
                        .subscribe(
                                searchInfoEntities -> {
                                    currentlyShownSearchInfo = searchInfoEntities;
                                    mSearchLocationView.bindSearchInfo(searchInfoEntities);
                                },
                                err -> mToastHelper.showShortToast("검색중 오류가 발생했습니다"),
                                () -> {}
                        )
        );
    }

    @Override
    public void onLocationItemClicked(int position) {
        SearchInfoEntity entity = currentlyShownSearchInfo.get(position);
        if (!entity.isFromHistory()) addToSearchHistory(entity);
        Intent intent = new Intent();
        intent.putExtra("reportName", entity.getName());
        intent.putExtra("reportAddress", entity.getMainAddress());
        intent.putExtra("reportLat", entity.getMapLat());
        intent.putExtra("reportLng", entity.getMapLng());
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void onDeleteHistoryClicked() {
        mCompositeDisposable.add(
                mSearchLocationRepository
                        .deleteSearchHistory(currentlyShownSearchInfo)
                        .subscribe(
                                e -> {
                                    if (e > 0) {
                                        mToastHelper.showShortToast("검색 목록이 삭제되었습니다");
                                        mSearchLocationView.clearHistory();
                                    }
                                },
                                err -> mToastHelper.showShortToast("삭제중 오류가 발생했습니다"),
                                () -> {}
                        )
        );
    }

    @Override
    public void onDeleteHistoryClicked(int position) {

        mCompositeDisposable.add(
                mSearchLocationRepository
                        .deleteFromSearchHistory(currentlyShownSearchInfo.get(position))
                        .subscribe(
                                e -> {
                                    if (e > 0) {
                                        currentlyShownSearchInfo.remove(position);
                                        mSearchLocationView.deleteHistoryItem(position);
                                        mToastHelper.showShortToast("검색 항목이 삭제되었습니다");
                                    }
                                },
                                err -> mToastHelper.showShortToast("삭제중 오류가 발생했습니다"),
                                () -> {}
                        )
        );
    }

}
