package com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class AddOnScrollRecyclerView extends RecyclerView {

    private int visibleThreshold = 1;
    private boolean isGridLayout;
    private LayoutManager layoutManager;
    private OnLoadMoreListener onLoadMoreListener;

    public AddOnScrollRecyclerView(Context context) {
        super(context);
    }

    public AddOnScrollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        addOnScrollListener(scrollListener);
    }

    public void setLayoutManager(LayoutManager layout, boolean isGridLayout) {
        super.setLayoutManager(layout);
        this.layoutManager = layout;
        this.isGridLayout = isGridLayout;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = 0;
            int totalItemCount = 0;
            int firstOrLastVisibleItem = 0;

            if (isGridLayout) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                firstOrLastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
            } else {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstOrLastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            }

            if (totalItemCount == visibleItemCount) {
                if (onLoadMoreListener != null)
                onLoadMoreListener.loadMore();
                return;
            }

            if ((totalItemCount - visibleItemCount) <= firstOrLastVisibleItem + visibleThreshold) {
                if (onLoadMoreListener != null)
                onLoadMoreListener.loadMore();
            }
        }
    };


}
