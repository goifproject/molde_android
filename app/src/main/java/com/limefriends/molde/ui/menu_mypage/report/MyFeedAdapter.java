package com.limefriends.molde.ui.menu_mypage.report;

import android.content.Context;
import android.content.Intent;
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
import com.limefriends.molde.entity.feed.MoldeFeedEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    // private List<MyPageMyReportEntity> myPageMyReportEntityList;

    private List<MoldeFeedEntity> entities = new ArrayList<>();

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void OnItemClick(int feedId, int position);
    }


    public MyFeedAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public class MyPageMyReportViewHorder extends RecyclerView.ViewHolder {
        @BindView(R.id.mypage_report_layout)
        RelativeLayout mypage_report_layout;
        @BindView(R.id.mypage_report_map)
        ImageView mypage_report_map;
        @BindView(R.id.mypage_report_date)
        TextView mypage_report_date;
        @BindView(R.id.mypage_report_location)
        TextView mypage_report_location;

        public MyPageMyReportViewHorder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void addData(List<MoldeFeedEntity> data) {
        entities.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public MyPageMyReportViewHorder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mypage_my_report_item, parent, false);
        return new MyPageMyReportViewHorder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("데이터 확인", entities.get(position).getRepDate());
        final MyPageMyReportViewHorder viewHorder = (MyPageMyReportViewHorder) holder;

        if (entities.get(position).getRepImg() != null && entities.get(position).getRepImg().size() != 0) {
            Glide.with(context).load(entities.get(position).getRepImg().get(0).getFilepath()).into(viewHorder.mypage_report_map);
        } else {
            Glide.with(context).load(R.drawable.report_map_img).into(viewHorder.mypage_report_map);
        }


        viewHorder.mypage_report_date.setText(entities.get(position).getRepDate());
        viewHorder.mypage_report_location.setText(entities.get(position).getRepAddr());

        viewHorder.mypage_report_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick(entities.get(position).getRepId(), position);
            }
        });
    }

    public void removeItem(int position) {
        entities.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }
}