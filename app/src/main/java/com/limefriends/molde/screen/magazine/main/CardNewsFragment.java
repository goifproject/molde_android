package com.limefriends.molde.screen.magazine.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.screen.common.addOnListview.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.addOnListview.OnLoadMoreListener;
import com.limefriends.molde.common.utils.NetworkUtil;
import com.limefriends.molde.model.entity.news.CardNewsEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.controller.BaseFragment;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.views.ViewFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class CardNewsFragment extends BaseFragment {

    @BindView(R.id.cardnews_recyclerView)
    AddOnScrollRecyclerView cardnews_recyclerView;
    @BindView(R.id.manual_new_molca)
    LinearLayout manual_new_molca;
    @BindView(R.id.manual_by_location)
    LinearLayout manual_by_location;
    @BindView(R.id.manual_for_spreading)
    LinearLayout manual_for_spreading;

    @Service
    private Repository.CardNews mCardNewsUseCase;
    @Service
    private ActivityScreenNavigator mActivityScreenNavigator;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private CardNewsAdapter cardNewsAdapter;

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

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_cardnews, container, false);

        setupViews(rootView);

        setupListeners();

        setupMagazineList();

        // 처음 한 번만 호출된다
        if (isFirstOnCreateView) loadMagazine(PER_PAGE, FIRST_PAGE);

        return rootView;
    }

    //-----
    // View
    //-----

    private void setupViews(View rootView) {
        ButterKnife.bind(this, rootView);
        manual_new_molca.setElevation(8);
        manual_for_spreading.setElevation(8);
        manual_by_location.setElevation(8);
    }

    private void setupListeners() {
        manual_new_molca.setOnClickListener(v -> mActivityScreenNavigator.toRecentMolcaInfoActivity());

        manual_by_location.setOnClickListener(v -> mActivityScreenNavigator.toHowToDetectActivity());

        manual_for_spreading.setOnClickListener(v -> mActivityScreenNavigator.toHowToRespondActivity());
    }

    private void setupMagazineList() {
        if (cardNewsAdapter == null) {
            cardNewsAdapter = new CardNewsAdapter(getContext());
        }
        // 1. 어댑터
        cardnews_recyclerView.setAdapter(cardNewsAdapter);
        // 2. 매니저
        cardnews_recyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2), true);
        // 3. loadMore
        cardnews_recyclerView.setOnLoadMoreListener(
                new OnLoadMoreListener() {
                    @Override
                    public void loadMore() {
                        loadMagazine(PER_PAGE, currentPage);
                    }

                    @Override
                    public boolean isLoading() {
                        return isLoading;
                    }
                });
    }

    //-----
    // Network
    //-----

    private void loadMagazine(int perPage, int page) {

        if (!NetworkUtil.isConnected(getContext())) {
            Toast.makeText(getContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. 더 이상 불러올 데이터가 없는지 확인
        if (!hasMoreToLoad) return;

        // 3. 스크롤에 의해서 다시 호출될 수 있기 때문에 로딩중임을 명시해 줌
        isLoading = true;

        List<CardNewsEntity> data = new ArrayList<>();

        mCompositeDisposable.add(
                mCardNewsUseCase
                        .getCardNewsList(perPage, page)
                        .subscribeWith(getDisposable())
        );
    }

    private void hasMoreToLoad(boolean hasMore) {
        hasMoreToLoad = hasMore;
    }

    private DisposableObserver<List<CardNewsEntity>> getDisposable() {
        List<CardNewsEntity> data = new ArrayList<>();
        return new DisposableObserver<List<CardNewsEntity>>() {
            @Override
            public void onNext(List<CardNewsEntity> newsEntityList) {
                data.addAll(newsEntityList);
            }

            @Override
            public void onError(Throwable e) {
                isFirstOnCreateView = false;
            }

            @Override
            public void onComplete() {
                isFirstOnCreateView = false;
                if (data.size() == 0) {
                    isLoading = false;
                    hasMoreToLoad(false);
                    return;
                } else if (data.size() < PER_PAGE) {
                    hasMoreToLoad(false);
                }
                cardNewsAdapter.addData(data);
                currentPage++;
                isLoading = false;
            }
        };
    }

    //-----
    // lifecycle
    //-----

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeDisposable.clear();
    }

}
