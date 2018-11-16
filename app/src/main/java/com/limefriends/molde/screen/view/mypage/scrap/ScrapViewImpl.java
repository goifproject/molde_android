package com.limefriends.molde.screen.view.mypage.scrap;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import java.util.List;

public class ScrapViewImpl
        extends BaseObservableView<ScrapView.Listener> implements ScrapView, RecyclerViewAdapter.OnItemClickListener {

    private AddOnScrollRecyclerView myScrap_recyclerView;
    private ProgressBar progressBar;
    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;

    private ViewFactory mViewFactory;
    private RecyclerViewAdapter<CardNewsEntity> mScrapAdapter;

    public ScrapViewImpl(LayoutInflater inflater,
                           ViewGroup parent,
                           ViewFactory viewFactory) {
        setRootView(inflater.inflate(R.layout.activity_my_scrap, parent, false));

        this.mViewFactory = viewFactory;

        setupViews();

        setupToolbar();

        setupListener();

        setupScrapList();
    }

    private void setupViews() {
        myScrap_recyclerView = findViewById(R.id.myScrap_recyclerView);
        progressBar = findViewById(R.id.progressBar3);
        mToolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
        mNestedToolbar.setTitle(getContext().getText(R.string.myscrap).toString());
    }

    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        myScrap_recyclerView.setOnLoadMoreListener(() -> {
            for (Listener listener : getListeners()) {
                listener.onLoadMore();
            }
        });
    }

    private void setupScrapList() {
        mScrapAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.CARDNEWS);
        myScrap_recyclerView.setAdapter(mScrapAdapter);
        myScrap_recyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2), true);
        mScrapAdapter.setOnItemClickListener(this);
    }

    @Override
    public void showProgressIndication() {
        progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void hideProgressIndication() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void bindScrap(List<CardNewsEntity> scrapList) {
        mScrapAdapter.addData(scrapList);
    }

    @Override
    public void deleteScrap(CardNewsEntity entity) {
        mScrapAdapter.removeData(entity);
    }

    @Override
    public void onItemClicked(int cardNewsId) {
        for (Listener listener : getListeners()) {
            listener.onScrapClicked(cardNewsId);
        }
    }
}
