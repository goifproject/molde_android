package com.limefriends.molde.screen.feed.main.view;

import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.addOnRecycler.AddOnScrollRecyclerView;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

import java.util.List;

import static com.limefriends.molde.common.Constant.Feed.FEED_BY_DISTANCE;
import static com.limefriends.molde.common.Constant.Feed.FEED_BY_LAST;

public class FeedViewImpl
        extends BaseObservableView<FeedView.Listener> implements FeedView, RecyclerViewAdapter.OnItemClickListener {

    private ToggleButton feed_sort_toggle;
    private TextView feed_update_date;
    private TextView feed_update_text;
    private AddOnScrollRecyclerView feed_list;

    private RecyclerViewAdapter<FeedEntity> mFeedAdapter;
    private ViewFactory mViewFactory;

    public FeedViewImpl(LayoutInflater inflater, ViewGroup parent, ViewFactory viewFactory) {

        setRootView(inflater.inflate(R.layout.fragment_feed, parent, false));

        this.mViewFactory = viewFactory;

        setupViews();

        setupListener();

        setupFeedList();
    }

    private void setupViews() {

        feed_sort_toggle = findViewById(R.id.feed_sort_toggle);
        feed_update_date = findViewById(R.id.feed_update_date);
        feed_update_text = findViewById(R.id.feed_update_text);
        feed_list = findViewById(R.id.feed_list);

        feed_update_text.setText(getContext().getText(R.string.feed_date_recent));
    }

    private void setupListener() {

        feed_sort_toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                feed_update_text.setText(getContext().getText(R.string.feed_date_recent));
                feed_sort_toggle.setBackgroundDrawable(
                        getContext().getResources().getDrawable(R.drawable.ic_feed_toggle_on));
            } else {
                feed_update_text.setText(getContext().getText(R.string.feed_date_location));
                feed_sort_toggle.setBackgroundDrawable(
                        getContext().getResources().getDrawable(R.drawable.ic_feed_toggle_off));
            }

            for (Listener listener : getListeners()) {
                listener.onToggleChanged(isChecked);
            }
        });


        feed_list.setOnLoadMoreListener(() -> {
            for (Listener listener : getListeners()) {
                listener.onLoadMore();
            }
        });

    }

    private void setupFeedList() {

        if (mFeedAdapter == null) {
            mFeedAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.FEED);
            mFeedAdapter.setOnItemClickListener(this);
        }
        feed_list.setAdapter(mFeedAdapter);
        feed_list.setLayoutManager(new LinearLayoutManager(getContext()), false);
    }

    @Override
    public void setToggle(String standard) {
        if (standard.equals(FEED_BY_DISTANCE)) {
            feed_sort_toggle.setChecked(false);
            feed_sort_toggle.setBackgroundDrawable(
                    getContext().getResources().getDrawable(R.drawable.ic_feed_toggle_off));
        } else if (standard.equals(FEED_BY_LAST)){
            feed_sort_toggle.setChecked(true);
            feed_sort_toggle.setBackgroundDrawable(
                    getContext().getResources().getDrawable(R.drawable.ic_feed_toggle_on));
        }
    }

    @Override
    public void bindFeed(List<FeedEntity> feedList) {
        feed_update_date.setText(DateUtil.fromLongToDate(feedList.get(0).getRepDate()));
        mFeedAdapter.addData(feedList);
    }

    @Override
    public void clearFeed() {
        mFeedAdapter.clearData();
    }

    @Override
    public void onItemClicked(int itemId) {
        for (Listener listener : getListeners()) {
            listener.onItemClicked(itemId);
        }
    }

}
