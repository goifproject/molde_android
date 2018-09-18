package com.limefriends.molde.comm.custom.addOnListview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

public class AddOnScrollExpandableListView extends ExpandableListView {

    private boolean isLoading = false;
    private int visibleThreshold = 1;
    private OnLoadMoreListener onLoadMoreListener;

    public AddOnScrollExpandableListView(Context context) {
        super(context);
    }

    public AddOnScrollExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnScrollListener(scrollListener);
    }

    public void setOnLoadMoreListener (OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (totalItemCount == visibleItemCount) return;

            if (!isLoading &&
                    (totalItemCount - visibleItemCount) <= firstVisibleItem + visibleThreshold) {
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.loadMore();
                }
                isLoading = true;
            }
        }
    };

}
