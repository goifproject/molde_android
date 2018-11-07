package com.limefriends.molde.screen.map.favorite.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.favorite.FavoriteEntity;
import com.limefriends.molde.screen.common.recyclerview.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerview.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.views.BaseObservableView;
import com.limefriends.molde.screen.common.views.ViewFactory;

import java.util.List;


public class FavoriteViewImpl
        extends BaseObservableView<FavoriteView.Listener>
        implements FavoriteView, RecyclerViewAdapter.OnItemClickListener, RecyclerViewAdapter.OnItem2ClickListener {



    private AddOnScrollRecyclerView my_favorite_list_view;
    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;

    private ViewFactory mViewFactory;
    private RecyclerViewAdapter<FavoriteEntity> mCommentAdapter;

    public FavoriteViewImpl(LayoutInflater inflater,
                            ViewGroup parent,
                            ViewFactory viewFactory) {
        setRootView(inflater.inflate(R.layout.activity_map_favorite, parent, false));

        this.mViewFactory = viewFactory;

        setupViews();

        setupToolbar();

        setupListener();

        setupFavoriteList();
    }


    private void setupViews() {
        my_favorite_list_view = findViewById(R.id.my_favorite_list_view);
        mToolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mNestedToolbar.setTitle(getContext().getText(R.string.my_favorite).toString());
        mToolbar.addView(mNestedToolbar.getRootView());
    }

    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        my_favorite_list_view.setOnLoadMoreListener(() -> {
            for (Listener listener : getListeners()) {
                listener.onLoadMore();
            }
        });
    }

    private void setupFavoriteList() {

        mCommentAdapter = new RecyclerViewAdapter<>(mViewFactory);
        mCommentAdapter.setOnItemClickListener(this);
        mCommentAdapter.setOnItem2ClickListener(this);
        my_favorite_list_view.setAdapter(mCommentAdapter);
        my_favorite_list_view.setLayoutManager(new LinearLayoutManager(getContext()), false);
    }

    // go to feed
    @Override
    public void onItemClicked(int itemId) {
        for (Listener listener : getListeners()) {
            listener.onFavoriteClicked(itemId);
        }
    }

    // delete feed
    @Override
    public void onItem2Clicked(int itemId) {
        for (Listener listener : getListeners()) {
            listener.onFavoriteDeleteClicked(itemId);
        }
    }

    @Override
    public void bindFavorites(List<FavoriteEntity> entityList) {
        mCommentAdapter.addData(entityList);
    }

    @Override
    public void removeFavorite(FavoriteEntity favoriteEntity) {
        mCommentAdapter.removeData(favoriteEntity);
    }
}
