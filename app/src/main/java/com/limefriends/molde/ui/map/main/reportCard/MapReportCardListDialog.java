package com.limefriends.molde.ui.map.main.reportCard;

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
import com.limefriends.molde.entity.FromSchemaToEntitiy;
import com.limefriends.molde.entity.feed.FeedEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.FeedResponseInfoEntityList;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapReportCardListDialog extends BottomSheetDialogFragment {

    @BindView(R.id.report_history_list_view)
    RecyclerView report_history_list_view;


    private MapReportCardListDialogAdapter reportHistoryAdapter;

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

        report_history_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        reportHistoryAdapter = new MapReportCardListDialogAdapter(getContext());
        report_history_list_view.setAdapter(reportHistoryAdapter);

        return view;
    }

    public void setData(int reportId) {

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<FeedResponseInfoEntityList> call = feedService.getFeedById(reportId);

        call.enqueue(new Callback<FeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<FeedResponseInfoEntityList> call, Response<FeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<FeedEntity> entityList = FromSchemaToEntitiy.feed(response.body().getData());
                    reportHistoryAdapter.setData(entityList);
                }
            }

            @Override
            public void onFailure(Call<FeedResponseInfoEntityList> call, Throwable t) {

            }
        });

    }





}
