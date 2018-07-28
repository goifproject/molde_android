package com.limefriends.molde.menu_mypage.report;

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
import com.limefriends.molde.menu_mypage.entity.MyPageMyReportEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPageMyReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MyPageMyReportEntity> myPageMyReportEntityList;


    public MyPageMyReportAdapter(Context context, List<MyPageMyReportEntity> myPageMyReportEntityList) {
        this.context = context;
        this.myPageMyReportEntityList = myPageMyReportEntityList;
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

    @Override
    public MyPageMyReportViewHorder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mypage_my_report_item, parent, false);
        return new MyPageMyReportViewHorder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyPageMyReportViewHorder viewHorder = (MyPageMyReportViewHorder) holder;
        Glide.with(context).load(myPageMyReportEntityList.get(position).getMyReport_map()).into(viewHorder.mypage_report_map);
        viewHorder.mypage_report_date.setText(myPageMyReportEntityList.get(position).getMyReport_date());
        viewHorder.mypage_report_location.setText(myPageMyReportEntityList.get(position).getMyReport_location());

        viewHorder.mypage_report_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyPageMyReportDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myPageMyReportEntityList.size();
    }
}
