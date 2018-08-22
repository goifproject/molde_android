package com.limefriends.molde.ui.map.main.card;

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
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.ui.feed.FeedDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportCardMapDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
//    private ArrayList<ReportMapEntitiy> reportMapEntitiyList;

    private List<FeedEntity> feedList = new ArrayList<>();

//    public MoldeReportCardMapDialogAdapter(Context context, ArrayList<ReportMapEntitiy> reportMapEntitiyList) {
//        this.context = context;
//        this.reportMapEntitiyList = reportMapEntitiyList;
//    }

    public MoldeReportCardMapDialogAdapter(Context context) {
        this.context = context;
    }

    public static class ReportMapViewHolder extends RecyclerView.ViewHolder {
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

        public ReportMapViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setData(List<FeedEntity> feedList) {
        this.feedList = feedList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_report_info_item, parent, false);
        return new ReportMapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ReportMapViewHolder) {
            final ReportMapViewHolder viewHolder = (ReportMapViewHolder) holder;
            viewHolder.report_info_date.setText(feedList.get(position).getRepDate());
            viewHolder.report_info_address.setText(feedList.get(position).getRepAddr());
            viewHolder.report_info_detail_address.setText(feedList.get(position).getRepDetailAddr());
            if (feedList.get(0).getRepImg() != null && feedList.get(0).getRepImg().size() != 0) {
                Glide.with(context).load(feedList.get(0).getRepImg().get(0).getFilepath()).into(viewHolder.report_info_thumbnail_image);
            } else {
                Glide.with(context).load(R.drawable.img_cardnews_dummy).into(viewHolder.report_info_thumbnail_image);
            }
            viewHolder.report_info_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO 네트워크 캐싱을 어떻게 할 것인지 고민하고 찾아보자
                    Intent intent = new Intent(context, FeedDetailActivity.class);
//                    intent.putExtra("", feedList.get(position).getRepImg());
//                    intent.putExtra("", feedList.get(position).getRepAddr());
//                    intent.putExtra("", feedList.get(position).getRepContents());
//                    intent.putExtra("", feedList.get(position).getRepState());
                    intent.putExtra("feedId", feedList.get(position).getRepId());
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }
}
