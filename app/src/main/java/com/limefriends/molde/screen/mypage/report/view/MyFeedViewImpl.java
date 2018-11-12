package com.limefriends.molde.screen.mypage.report.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import java.util.List;

public class MyFeedViewImpl
        extends BaseObservableView<MyFeedView.Listener>
        implements MyFeedView, RecyclerViewAdapter.OnItemClickListener {

    private AddOnScrollRecyclerView myReport_recyclerView;

    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;

    private RecyclerViewAdapter<FeedEntity> mFeedAdapter;
    private ViewFactory mViewFactory;


    public MyFeedViewImpl(LayoutInflater inflater,
                          ViewGroup parent,
                          ViewFactory viewFactory) {

        this.mViewFactory = viewFactory;

        setRootView(inflater.inflate(R.layout.activity_my_feed, parent, false));

        setupViews();

        setupToolbar();

        setupListener();

        setFeedRecycler();
    }

    private void setupViews() {
        myReport_recyclerView = findViewById(R.id.myReport_recyclerView);
        mToolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
        mNestedToolbar.setTitle(getContext().getText(R.string.myreport).toString());
    }

    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        myReport_recyclerView.setOnLoadMoreListener(() -> {
            for (Listener listener : getListeners()) {
                listener.onLoadMore();
            }
        });
    }

    private void setFeedRecycler() {
        mFeedAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.MY_FEED);
        myReport_recyclerView.setAdapter(mFeedAdapter);
        myReport_recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()), false);
        mFeedAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClicked(int position) {
        for (Listener listener : getListeners()) {
            listener.onMyFeedSelected(position);
        }
    }

    @Override
    public void bindMyFeed(List<FeedEntity> entityList) {
        mFeedAdapter.addData(entityList);
    }

    @Override
    public void deleteFeed(int position) {
        mFeedAdapter.removeData(position);
    }

    @Override
    public void updateFeed(int position, FeedEntity entity) {
        mFeedAdapter.updateData(position, entity);
    }
}
