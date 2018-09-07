package com.limefriends.molde.ui.map.main.reportCard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.DateUitl;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.ui.feed.FeedDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.comm.Constant.Feed.EXTRA_KEY_FEED_ID;

public class MapReportCardListDialogAdapter extends RecyclerView.Adapter<MapReportCardListDialogAdapter.ReportMapViewHolder> {

    private Context context;

    private List<FeedEntity> feedList = new ArrayList<>();

    MapReportCardListDialogAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<FeedEntity> feedList) {
        this.feedList = feedList;
        notifyDataSetChanged();
    }

    @Override
    public ReportMapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_feed_dialog, parent, false);
        return new ReportMapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReportMapViewHolder viewHolder, int position) {
        viewHolder.report_info_date.setText(DateUitl.fromLongToDate(feedList.get(position).getRepDate()));
        viewHolder.report_info_address.setText(feedList.get(position).getRepAddr());
        viewHolder.report_info_detail_address.setText(feedList.get(position).getRepDetailAddr());
        if (feedList.get(0).getRepImg() != null && feedList.get(0).getRepImg().size() != 0) {
            Glide.with(context)
                    .load(feedList.get(0).getRepImg().get(0).getFilepath())
                    .placeholder(R.drawable.img_placeholder_magazine)
                    .centerCrop()
                    .into(viewHolder.report_info_thumbnail_image);
        } else {
            Glide.with(context)
                    .load(R.drawable.img_placeholder_magazine)
                    .into(viewHolder.report_info_thumbnail_image);
        }
        viewHolder.position = position;
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    class ReportMapViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.report_info_layout)
        RelativeLayout report_info_layout;
        @BindView(R.id.report_info_thumbnail_image)
        ImageView report_info_thumbnail_image;
        @BindView(R.id.report_info_date)
        TextView report_info_date;
        @BindView(R.id.report_info_address)
        TextView report_info_address;
        @BindView(R.id.report_info_detail_address)
        TextView report_info_detail_address;

        int position;

        ReportMapViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FeedDetailActivity.class);
                    intent.putExtra(EXTRA_KEY_FEED_ID, feedList.get(position).getRepId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
