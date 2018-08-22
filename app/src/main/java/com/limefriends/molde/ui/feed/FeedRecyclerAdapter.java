package com.limefriends.molde.ui.feed;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
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
import com.limefriends.molde.entity.feed.FeedEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_PROG = 0;
    private static final int VIEW_ITEM = 1;
//    private boolean isMoreLoading = false;
//    private int visibleThreshold = 1;
//    private int firstVisibleItem, visibleItemCount, totalItemCount, lastVisibleItem;

    private Context context;
    private List<FeedEntity> reportFeedList;
//    private LinearLayoutManager layoutManager;
//    private OnLoadMoreListener onLoadMoreListener;
    private OnClickFeedItemListener onClickFeedItemListener;

    private Dialog feedDialogImage;
    private ImageView feed_dialog_thumbnail_image;
    private ImageButton feed_dialog_close_button;

//    public interface OnLoadMoreListener {
//        void onLoadMore();
//    }
//
    public interface OnClickFeedItemListener {
        void callFeedData(FeedEntity feedEntity);
    }

    public FeedRecyclerAdapter(Context context, OnClickFeedItemListener onClickFeedItemListener) {
        this.context = context;
//        this.onLoadMoreListener = onLoadMoreListener;
        this.onClickFeedItemListener = onClickFeedItemListener;
        this.reportFeedList = new ArrayList<>();
    }

    public void clear() {
        reportFeedList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<FeedEntity> newReportFeed) {
        int start = reportFeedList.size();
        reportFeedList.addAll(newReportFeed);
        notifyItemRangeInserted(start, newReportFeed.size());
    }

    @Override
    public int getItemViewType(int position) {
        return reportFeedList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
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

//    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
//        this.layoutManager = linearLayoutManager;
//    }

//    public void setRecyclerView(RecyclerView mView) {
//        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                visibleItemCount = recyclerView.getChildCount();
//                totalItemCount = layoutManager.getItemCount();
//                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//                Log.d("total:first:last:vis", totalItemCount + ":" + firstVisibleItem + ":" + lastVisibleItem+":"+visibleItemCount);
//                // Log.d("visible", visibleItemCount + "");
//                // Log.d("first", firstVisibleItem + "");
//                // Log.d("last", lastVisibleItem + "");
//
//                if (!isMoreLoading
//                        && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//                    // Log.e("더 가져오기", "5");
//                    if (onLoadMoreListener != null) {
//                        // Log.e("더 가져오기", "6");
//                        onLoadMoreListener.onLoadMore();
//                    }
//                    isMoreLoading = true;
//                }
//            }
//        });
//    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FeedViewHolder) {
            FeedViewHolder viewHolder = (FeedViewHolder) holder;
            if (reportFeedList.get(position).getRepImg() != null && reportFeedList.get(position).getRepImg().size() != 0) {
                Glide.with(context).load(reportFeedList.get(position).getRepImg().get(0).getFilepath())
                        .into(viewHolder.feed_image);
            } else {
                Glide.with(context).load(R.drawable.img_cardnews_dummy).into(viewHolder.feed_image);
            }
            viewHolder.feed_image.setClipToOutline(true);
            viewHolder.feed_image.setElevation(8);
            viewHolder.feed_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    feedDialogImage = new Dialog(context);
                    feedDialogImage.setContentView(R.layout.feed_image_dialog);
                    feedDialogImage.show();
                    feed_dialog_thumbnail_image = (ImageView) feedDialogImage.findViewById(R.id.feed_dialog_thumbnail_image);
                    feed_dialog_close_button = (ImageButton) feedDialogImage.findViewById(R.id.feed_dialog_close_button);
                    if (reportFeedList.get(position).getRepImg().size() != 0) {
                        Glide.with(context).load(reportFeedList.get(position).getRepImg().get(0).getFilename())
                                .into(feed_dialog_thumbnail_image);
                    }
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
            viewHolder.feed_address.setText(reportFeedList.get(position).getRepAddr());
            viewHolder.feed_detail_address.setText(reportFeedList.get(position).getRepDetailAddr());
            viewHolder.feed_date.setText(reportFeedList.get(position).getRepDate());
            switch (reportFeedList.get(position).getRepId()) {
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

//    public void setMoreLoading(boolean isMoreLoading) {
//        this.isMoreLoading = isMoreLoading;
//    }

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

//    public void addItemMore(ArrayList<FeedEntity> reportFeedList) {
//        this.reportFeedList.addAll(reportFeedList);
//        notifyItemRangeChanged(0, this.reportFeedList.size());
//    }

    class FeedViewHolder extends RecyclerView.ViewHolder {

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

        FeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupViews();
        }

        private void setupViews() {
            feed_image.setClipToOutline(true);
            feed_image.setElevation(8);
            feed_content.setElevation(12);
        }

//        private void setupListener() {
//
//        }
//
//        public void bindItem(FeedEntity entity) {
//            if (entity.getRepImg() != null && entity.getRepImg().size() != 0) {
//                Glide.with(context).load(entity.getRepImg().get(0).getFilepath()).into(feed_image);
//            } else {
//                Glide.with(context).load(R.drawable.img_cardnews_dummy).into(feed_image);
//            }
//            feed_address.setText(entity.getRepAddr());
//            feed_detail_address.setText(entity.getRepDetailAddr());
//            feed_date.setText(entity.getRepDate());
//        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar feedLoadingBar;

        ProgressViewHolder(View v) {
            super(v);
            feedLoadingBar = v.findViewById(R.id.feed_loading_bar);
        }
    }

}
