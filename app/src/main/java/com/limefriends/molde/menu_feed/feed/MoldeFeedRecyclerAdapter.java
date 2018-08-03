package com.limefriends.molde.menu_feed.feed;

import android.app.Dialog;
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
import com.limefriends.molde.menu_feed.entity.MoldeFeedEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeFeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ImageView feed_dialog_thumbnail_image;
    ImageButton feed_dialog_close_button;

    private final  int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private Context context;
    private ArrayList<MoldeFeedEntity> reportFeedList;
    private LinearLayoutManager layoutManager;
    private OnLoadMoreListener onLoadMoreListener;
    private OnClickFeedItemListener onClickFeedItemListener;
    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    public Dialog feedDialogImage;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnClickFeedItemListener {
        void callFeedData(MoldeFeedEntity feedEntitiy);
    }

    public MoldeFeedRecyclerAdapter(Context context,
                                    OnLoadMoreListener onLoadMoreListener,
                                    OnClickFeedItemListener onClickFeedItemListener) {
        this.context = context;
        this.onLoadMoreListener = onLoadMoreListener;
        this.onClickFeedItemListener = onClickFeedItemListener;
        this.reportFeedList = new ArrayList<MoldeFeedEntity>();
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
            FeedViewHolder viewHolder = (FeedViewHolder) holder;
            Glide.with(context)
                    .load(reportFeedList.get(position).getReportFeedThumbnail())
                    .into(viewHolder.feed_image);
            viewHolder.feed_image.setClipToOutline(true);
            viewHolder.feed_image.setElevation(8);
            viewHolder.feed_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedDialogImage = new Dialog(context);
                    feedDialogImage.setContentView(R.layout.feed_image_dialog);
                    feedDialogImage.show();
                    feed_dialog_thumbnail_image = (ImageView)  feedDialogImage.findViewById(R.id.feed_dialog_thumbnail_image);
                    feed_dialog_close_button = (ImageButton) feedDialogImage.findViewById(R.id.feed_dialog_close_button);
                    Glide.with(context)
                            .load(reportFeedList.get(position).getReportFeedThumbnail())
                            .into(feed_dialog_thumbnail_image);
                    feed_dialog_close_button.bringToFront();
                    feed_dialog_close_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            feedDialogImage.dismiss();
                        }
                    });
                }
            });
            viewHolder.feed_content.setElevation(12);
            viewHolder.feed_address.setText(reportFeedList.get(position).getReportFeedAddress());
            viewHolder.feed_detail_address.setText(reportFeedList.get(position).getReportFeedDetailAddress());
            viewHolder.feed_date.setText(reportFeedList.get(position).getReportFeedDate());
            switch (reportFeedList.get(position).getReportFeedMarkerId()) {
                case 0:
                    viewHolder.feed_state.setImageResource(R.drawable.ic_marker_red);
                    break;
                case 1:
                    viewHolder.feed_state.setImageResource(R.drawable.ic_marker_green);
                    break;
                case 2:
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

    public void addAll(ArrayList<MoldeFeedEntity> reportFeedList) {
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

    public void addItemMore(ArrayList<MoldeFeedEntity> reportFeedList) {
        this.reportFeedList.addAll(reportFeedList);
        notifyItemRangeChanged(0, this.reportFeedList.size());
    }

}
