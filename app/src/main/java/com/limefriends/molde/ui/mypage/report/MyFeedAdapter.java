package com.limefriends.molde.ui.mypage.report;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.feed.FeedEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFeedAdapter extends RecyclerView.Adapter<MyFeedAdapter.MyPageMyReportViewHolder> {

    private Context context;
    private List<FeedEntity> feedEntities = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void OnItemClick(int feedId, int position);
    }

    MyFeedAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void addData(List<FeedEntity> data) {
        feedEntities.addAll(data);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        feedEntities.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public MyPageMyReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.mypage_my_report_item, parent, false);
        return new MyPageMyReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPageMyReportViewHolder viewHorder, int position) {
        FeedEntity feed = feedEntities.get(position);
        if (feed.getRepImg() != null &&
                feed.getRepImg().size() != 0) {
            Glide.with(context)
                    .load(feed.getRepImg().get(0).getFilepath())
                    .placeholder(R.drawable.report_map_img)
                    .into(viewHorder.mypage_report_map);
        } else {
            Glide.with(context)
                    .load(R.drawable.report_map_img)
                    .into(viewHorder.mypage_report_map);
        }
        viewHorder.mypage_report_date.setText(feed.getRepDate());
        viewHorder.mypage_report_location.setText(feed.getRepAddr());
        viewHorder.position = position;
    }


    @Override
    public int getItemCount() {
        return feedEntities.size();
    }

    class MyPageMyReportViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.mypage_report_layout)
        RelativeLayout mypage_report_layout;
        @BindView(R.id.mypage_report_map)
        ImageView mypage_report_map;
        @BindView(R.id.mypage_report_date)
        TextView mypage_report_date;
        @BindView(R.id.mypage_report_location)
        TextView mypage_report_location;

        int position;

        MyPageMyReportViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnItemClick(feedEntities.get(position).getRepId(), position);
                }
            });
        }
    }
}