package com.limefriends.molde.screen.common.addOnListview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

public class AddOnScrollExpandableListView extends ExpandableListView {

    private int visibleThreshold = 1;
    private OnLoadMoreListener onLoadMoreListener;

    public AddOnScrollExpandableListView(Context context) {
        super(context);
    }

    public AddOnScrollExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOnScrollListener(scrollListener);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    private OnScrollListener scrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            if (totalItemCount == visibleItemCount) return;

            if ((totalItemCount - visibleItemCount) <= firstVisibleItem + visibleThreshold) {
                onLoadMoreListener.loadMore();
            }
        }
    };

}
