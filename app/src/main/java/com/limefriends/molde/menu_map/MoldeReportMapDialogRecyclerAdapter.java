package com.limefriends.molde.menu_map;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.entity.MoldeReportMapEntitiy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportMapDialogRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<MoldeReportMapEntitiy> reportMapEntitiyList;


    public MoldeReportMapDialogRecyclerAdapter(Context context, ArrayList<MoldeReportMapEntitiy> reportMapEntitiyList) {
        this.context = context;
        this.reportMapEntitiyList = reportMapEntitiyList;
    }

    public static class ReportMapViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.report_info_thumbnail_image)
        ImageButton report_info_thumbnail_image;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.map_list_item_report_info, parent, false);
        return new ReportMapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ReportMapViewHolder){
            final ReportMapViewHolder viewHolder = (ReportMapViewHolder) holder;
            viewHolder.report_info_date.setText(reportMapEntitiyList.get(position).getReportDate());
            viewHolder.report_info_address.setText(reportMapEntitiyList.get(position).getReportAddress());
            viewHolder.report_info_detail_address.setText(reportMapEntitiyList.get(position).getReportDetailAddress());
        }

    }

    @Override
    public int getItemCount() {
        return reportMapEntitiyList.size();
    }
}
