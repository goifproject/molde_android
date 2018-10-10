package com.limefriends.molde.ui.mypage.report;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.Constant;
import com.limefriends.molde.comm.utils.DateUtil;
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

    public void updateItem(int position, int state) {
        feedEntities.get(position).setRepState(state);
        notifyDataSetChanged();
    }

    @Override
    public MyPageMyReportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_my_feed, parent, false);
        return new MyPageMyReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyPageMyReportViewHolder viewHolder, int position) {
        FeedEntity feed = feedEntities.get(position);
        if (feed.getRepImg() != null &&
                feed.getRepImg().size() != 0) {
            Glide.with(context)
                    .load(feed.getRepImg().get(0).getFilepath())
                    .placeholder(R.drawable.img_placeholder_my_feed)
                    .into(viewHolder.mypage_report_map);
        } else {
            Glide.with(context)
                    .load(R.drawable.img_placeholder_my_feed)
                    .into(viewHolder.mypage_report_map);
        }
        switch (feed.getRepState()) {
            case Constant.ReportState.RECEIVING:
                viewHolder.report_progress_dot_second_yellow
                        .setVisibility(View.INVISIBLE);
                viewHolder.report_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                viewHolder.report_progress_line_first.setBackgroundColor(
                        context.getResources().getColor(R.color.colorDivision));
                viewHolder.report_progress_line_second.setBackgroundColor(
                        context.getResources().getColor(R.color.colorDivision));
                viewHolder.report_progress_text_accepted
                        .setTextColor(context.getResources().getColor(R.color.colorDivision));
                viewHolder.report_progress_text_completed
                        .setTextColor(context.getResources().getColor(R.color.colorDivision));
                break;
            case Constant.ReportState.ACCEPTED:
                viewHolder.report_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                viewHolder.report_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                viewHolder.report_progress_line_first.setBackgroundColor(
                        context.getResources().getColor(R.color.colorAccent));
                viewHolder.report_progress_line_second.setBackgroundColor(
                        context.getResources().getColor(R.color.colorDivision));
                viewHolder.report_progress_text_accepted
                        .setTextColor(context.getResources().getColor(R.color.colorInfoTextColor));
                viewHolder.report_progress_text_completed
                        .setTextColor(context.getResources().getColor(R.color.colorDivision));
                break;
            case Constant.ReportState.FOUND:
            case Constant.ReportState.CLEAN:
                viewHolder.report_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                viewHolder.report_progress_dot_third_yellow
                        .setVisibility(View.VISIBLE);
                viewHolder.report_progress_line_first.setBackgroundColor(
                        context.getResources().getColor(R.color.colorAccent));
                viewHolder.report_progress_line_second.setBackgroundColor(
                        context.getResources().getColor(R.color.colorAccent));
                viewHolder.report_progress_text_accepted
                        .setTextColor(context.getResources().getColor(R.color.colorInfoTextColor));
                viewHolder.report_progress_text_completed
                        .setTextColor(context.getResources().getColor(R.color.colorInfoTextColor));
                break;
        }
        viewHolder.mypage_report_date.setText(DateUtil.fromLongToDate(feed.getRepDate()));
        viewHolder.mypage_report_location.setText(feed.getRepAddr());
        viewHolder.mypage_report_address.setText(feed.getRepDetailAddr());
        viewHolder.position = position;
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
        @BindView(R.id.mypage_report_address)
        TextView mypage_report_address;

        @BindView(R.id.report_progress_line_first)
        View report_progress_line_first;
        @BindView(R.id.report_progress_line_second)
        View report_progress_line_second;
        @BindView(R.id.report_progress_dot_first)
        ImageView report_progress_dot_first;
        @BindView(R.id.report_progress_dot_second_yellow)
        ImageView report_progress_dot_second_yellow;
        @BindView(R.id.report_progress_dot_third_yellow)
        ImageView report_progress_dot_third_yellow;

        @BindView(R.id.report_progress_text_accepted)
        TextView report_progress_text_accepted;
        @BindView(R.id.report_progress_text_completed)
        TextView report_progress_text_completed;

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