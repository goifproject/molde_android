package com.limefriends.molde.comm.custom.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.limefriends.molde.R;
import com.limefriends.molde.ui.menu_magazine.MagazineCardNewsAdapter;

public class AddOnScrollRecyclerView2 extends LinearLayout {

    RecyclerView recyclerView;
    ProgressBar progressBar;

    private boolean isLoading = false;
    private int visibleThreshold = 1;
    private boolean isGridLayout;
    private RecyclerView.LayoutManager layoutManager;
    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickedListener onItemClickedListener;


    public interface OnLoadMoreListener {
        void loadMore();
    }

    public interface OnItemClickedListener<T> {
        void itemClicked(T item);
    }

    public AddOnScrollRecyclerView2(Context context) {
        super(context);
    }

    public AddOnScrollRecyclerView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        View view = LayoutInflater.from(context).inflate(R.layout.addon_recyclerview, this, false);

        recyclerView = view.findViewById(R.id.addon_recyclerview);
        progressBar = view.findViewById(R.id.addon_progressbar);

        recyclerView.addOnScrollListener(scrollListener);

        addView(view);

    }

    public void setLayoutManager(RecyclerView.LayoutManager layout, boolean isGridLayout) {
        recyclerView.setLayoutManager(layout);
        this.layoutManager = layout;
        this.isGridLayout = isGridLayout;
    }

    public int getVisibleThreshold() {
        return visibleThreshold;
    }

    public void setVisibleThreshold(int threshold) {
        if (threshold >= 10) return;
        this.visibleThreshold = threshold;
    }

    public void setOnLoadMoreListener (OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.onItemClickedListener = listener;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = 0;
            int totalItemCount = 0;
            int firstOrLastVisibleItem = 0;

            if (isGridLayout) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                // int spanCount = gridLayoutManager.getSpanCount();
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                firstOrLastVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();
                // Log.d("tot:fir:vis:vis2-G", totalItemCount + ":" + firstOrLastVisibleItem + ":" + visibleItemCount+":"+layoutManager.getChildCount());
            } else {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                firstOrLastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                // Log.d("tot:fir:vis:vis2-L", totalItemCount + ":" + firstVisibleItem + ":" + visibleItemCount+":"+layoutManager.getChildCount());
            }

            if (totalItemCount == visibleItemCount) return;

            if (!isLoading &&
                    (totalItemCount - visibleItemCount) <= firstOrLastVisibleItem + visibleThreshold) {
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.loadMore();
                }
                isLoading = true;
            }
        }
    };

    MagazineCardNewsAdapter adapter;

    public void setAdapter(MagazineCardNewsAdapter adatper) {
        this.adapter  = adatper;
    }


}
