package com.limefriends.molde.menu_feed;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_feed.entity.MoldeFeedEntitiy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeFeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private Context context;
    private ArrayList<MoldeFeedEntitiy> reportFeedList;
    private LinearLayoutManager layoutManager;
    private OnLoadMoreListener onLoadMoreListener;
    private OnClickFeedItemListener onClickFeedItemListener;
    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnClickFeedItemListener {
        void callFeedData(MoldeFeedEntitiy feedEntitiy);
    }

    public MoldeFeedRecyclerAdapter(Context context,
                                    OnLoadMoreListener onLoadMoreListener,
                                    OnClickFeedItemListener onClickFeedItemListener) {
        this.context = context;
        this.onLoadMoreListener = onLoadMoreListener;
        this.onClickFeedItemListener = onClickFeedItemListener;
        this.reportFeedList = new ArrayList<MoldeFeedEntitiy>();
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.layoutManager = linearLayoutManager;
    }

    public void setRecyclerView(RecyclerView mView) {
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                Log.d("total", totalItemCount + "");
                Log.d("visible", visibleItemCount + "");
                Log.d("first", firstVisibleItem + "");
                Log.d("last", lastVisibleItem + "");

                if (!isMoreLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isMoreLoading = true;
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.feed_list_item_report_info, parent, false);
            return new FeedViewHolder(view);
        } else {
            return new ProgressViewHolder(LayoutInflater.from(context).inflate(R.layout.feed_loading_progress, parent, false));
        }
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.feed_content)
        RelativeLayout feed_content;
        @BindView(R.id.feed_image)
        ImageButton feed_image;
        @BindView(R.id.feed_address)
        TextView feed_address;
        @BindView(R.id.feed_detail_address)
        TextView feed_detail_address;
        @BindView(R.id.feed_date)
        TextView feed_date;
        @BindView(R.id.feed_state)
        ImageView feed_state;

        public FeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FeedViewHolder) {
            final FeedViewHolder viewHolder = (FeedViewHolder) holder;
            Glide.with(context)
                    .load("http://via.placeholder.com/300.png")
                    //.bitmapTransform(new RoundedCornersTransformation(context, 30, 10))
                    .into(viewHolder.feed_image);
            viewHolder.feed_image.setClipToOutline(true);
            viewHolder.feed_content.setElevation(8);
            viewHolder.feed_address.setText(reportFeedList.get(position).getReportFeedAddress());
            switch (reportFeedList.get(position).getReportFeedMarkerId()) {
                case 1:
                    viewHolder.feed_state.setImageResource(R.drawable.ic_marker_red);
                    break;
                case 2:
                    viewHolder.feed_state.setImageResource(R.drawable.ic_marker_green);
                    break;
                case 3:
                    viewHolder.feed_state.setImageResource(R.drawable.ic_marker_white);
                    break;
            }
            viewHolder.feed_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickFeedItemListener.callFeedData(reportFeedList.get(position));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return reportFeedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return reportFeedList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void addAll(ArrayList<MoldeFeedEntitiy> reportFeedList) {
        this.reportFeedList.clear();
        this.reportFeedList.addAll(reportFeedList);
        notifyDataSetChanged();
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar feedLoadingBar;

        public ProgressViewHolder(View v) {
            super(v);
            feedLoadingBar = (ProgressBar) v.findViewById(R.id.feed_loading_bar);
        }
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading = isMoreLoading;
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    reportFeedList.add(null);
                    notifyItemInserted(reportFeedList.size() - 1);
                }
            });
        } else {
            reportFeedList.remove(reportFeedList.size() - 1);
            notifyItemRemoved(reportFeedList.size());
        }
    }

    public void addItemMore(ArrayList<MoldeFeedEntitiy> reportFeedList) {
        this.reportFeedList.addAll(reportFeedList);
        notifyItemRangeChanged(0, this.reportFeedList.size());
    }

}
