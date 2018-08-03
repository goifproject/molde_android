package com.limefriends.molde.menu_map.reportcard;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_map.entity.MoldeReportMapEntitiy;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeReportCardMapDialog extends BottomSheetDialogFragment {
    @BindView(R.id.report_history_list_view)
    RecyclerView report_history_list_view;

    private Context context;

    public MoldeReportCardMapDialogAdapter reportHistoryAdapter;
    public ArrayList<MoldeReportMapEntitiy> reportHistoryList;
    public ReportCardItem reportCardData;

    public static MoldeReportCardMapDialog getInstance() {
        return new MoldeReportCardMapDialog();
    }

    public void setData(ReportCardItem data){
        reportCardData = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
                View bottomSheetInternal = bottomSheetDialog.findViewById(android.support.design.R.id.design_bottom_sheet);
                assert bottomSheetInternal != null;
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        View view = inflater.inflate(R.layout.map_report_card_dialog, container,false);
        ButterKnife.bind(this, view);

        reportHistoryList = new ArrayList<MoldeReportMapEntitiy>();
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        report_history_list_view.setLayoutManager(layoutManager);
        reportHistoryAdapter = new MoldeReportCardMapDialogAdapter(getContext(), reportHistoryList);
        report_history_list_view.setAdapter(reportHistoryAdapter);
        return view;
    }



}
