package com.limefriends.molde.ui.feed;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.DateUtil;
import com.limefriends.molde.entity.feed.FeedEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.comm.Constant.ReportState.*;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private Context context;
    private List<FeedEntity> reportFeedList;
    private OnClickFeedItemListener onClickFeedItemListener;
    private FragmentManager fragmentManager;

    public interface OnClickFeedItemListener {
        void callFeedData(FeedEntity feedEntity);
    }

    FeedAdapter(Context context, OnClickFeedItemListener onClickFeedItemListener,
                FragmentManager fragmentManager) {
        this.context = context;
        this.onClickFeedItemListener = onClickFeedItemListener;
        this.reportFeedList = new ArrayList<>();
        this.fragmentManager = fragmentManager;
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
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder viewHolder, int position) {
        FeedEntity feed = reportFeedList.get(position);
        if (feed.getRepImg() != null && feed.getRepImg().size() != 0) {
            Glide.with(context).load(feed.getRepImg().get(0).getFilepath())
                    .placeholder(R.drawable.img_placeholder_magazine).into(viewHolder.feed_image);
        } else {
            Glide.with(context).load(R.drawable.img_placeholder_magazine).into(viewHolder.feed_image);
        }
        viewHolder.position = position;
        viewHolder.feed_address.setText(feed.getRepAddr());
        viewHolder.feed_detail_address.setText(feed.getRepDetailAddr());
        viewHolder.feed_date.setText(DateUtil.fromLongToDate(feed.getRepDate()));
        switch (reportFeedList.get(position).getRepState()) {
            case RECEIVING:
            case ACCEPTED:
                viewHolder.feed_state.setImageResource(R.drawable.ic_map_marker_red);
                break;
            case FOUND:
                viewHolder.feed_state.setImageResource(R.drawable.ic_map_marker_white);
                break;
            case CLEAN:
                viewHolder.feed_state.setImageResource(R.drawable.ic_map_marker_green);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return reportFeedList.size();
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.feed_content)
        RelativeLayout feed_content;
        @BindView(R.id.feed_image)
        ImageView feed_image;
        @BindView(R.id.feed_address)
        TextView feed_address;
        @BindView(R.id.feed_detail_address)
        TextView feed_detail_address;
        @BindView(R.id.feed_date)
        TextView feed_date;
        @BindView(R.id.feed_state)
        ImageView feed_state;

        private int position;

        FeedViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupViews();
            setupListener();
        }

        private void setupViews() {
            feed_image.setClipToOutline(true);
            feed_image.setElevation(8);
            feed_content.setElevation(12);
        }

        private void setupListener() {
            feed_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickFeedItemListener.callFeedData(reportFeedList.get(position));
                }
            });

            feed_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (reportFeedList.get(position).getRepImg().size() != 0) {
                        FeedImageDialog dialog = new FeedImageDialog();
                        dialog.show(fragmentManager, "imageDialog");
                        dialog.setData(reportFeedList.get(position).getRepImg().get(0).getFilepath());
                    } else {
                        FeedImageDialog dialog = new FeedImageDialog();
                        dialog.show(fragmentManager, "imageDialog");
                        dialog.setData(null);
                    }
                }
            });
        }
    }

}
