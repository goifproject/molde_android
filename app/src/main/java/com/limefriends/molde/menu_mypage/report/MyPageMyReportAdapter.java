package com.limefriends.molde.menu_mypage.report;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_mypage.entity.MyPageMyReportEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyPageMyReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int resourceId;
    private List<MyPageMyReportEntity> myPageMyReportEntityList;


    public MyPageMyReportAdapter(Context context, int resourceId, List<MyPageMyReportEntity> myPageMyReportEntityList) {
        this.context = context;
        this.resourceId = resourceId;
        this.myPageMyReportEntityList = myPageMyReportEntityList;
    }

    public class MyPageMyReportViewHorder extends RecyclerView.ViewHolder {

        @BindView(R.id.myReport_map)
        ImageView myReport_map;

        @BindView(R.id.myReport_date)
        TextView myReport_date;

        @BindView(R.id.myReport_location)
        TextView myReport_location;

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
        Glide.with(context).load(myPageMyReportEntityList.get(position).getMyReport_map()).into(viewHorder.myReport_map);
        viewHorder.myReport_date.setText(myPageMyReportEntityList.get(position).getMyReport_date());
        viewHorder.myReport_location.setText(myPageMyReportEntityList.get(position).getMyReport_location());
    }

    @Override
    public int getItemCount() {
        return myPageMyReportEntityList.size();
    }
}
