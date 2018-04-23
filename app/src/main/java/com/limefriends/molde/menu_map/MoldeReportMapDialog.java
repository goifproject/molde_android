package com.limefriends.molde.menu_map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_magazine.RecyclerAdapter;
import com.limefriends.molde.menu_map.entity.MoldeReportMapEntitiy;
import com.limefriends.molde.menu_map.reportCard.ReportCardItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportMapDialog extends BottomSheetDialogFragment {
    @BindView(R.id.report_history)
    RelativeLayout report_history;
    @BindView(R.id.report_history_list)
    RecyclerView report_history_list;

    private Context context;

    public MoldeReportMapDialogRecyclerAdapter reportHistoryAdapter;
    public ArrayList<MoldeReportMapEntitiy> reportHistoryList;
    public ReportCardItem reportCardData;

    public static MoldeReportMapDialog getInstance() { return new MoldeReportMapDialog(); }

    public void setData(ReportCardItem data){
        reportCardData = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_report_map_dialog, container,false);
        ButterKnife.bind(this, view);

        reportHistoryList = new ArrayList<MoldeReportMapEntitiy>();
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        report_history_list.setLayoutManager(layoutManager);
        reportHistoryAdapter = new MoldeReportMapDialogRecyclerAdapter(getContext(), reportHistoryList);
        report_history_list.setAdapter(reportHistoryAdapter);
        return view;
    }



}
