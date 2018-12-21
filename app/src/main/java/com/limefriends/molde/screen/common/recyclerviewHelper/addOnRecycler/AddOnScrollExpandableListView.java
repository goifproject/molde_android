package com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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

            if (totalItemCount == 0 || visibleItemCount == 0) return;

            if ((totalItemCount - visibleItemCount) <= firstVisibleItem + visibleThreshold) {
                // 어차피 여러번 호출되도 한 번 로딩 중에는 다시 로딩되지 않기 때문에 상관 없음
                onLoadMoreListener.loadMore();
            }
        }
    };

}
