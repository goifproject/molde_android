package com.limefriends.molde.ui.menu_map.main.card;

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
import com.limefriends.molde.entity.feed.MoldeFeedEntity;
import com.limefriends.molde.entity.feed.MoldeFeedResponseInfoEntity;
import com.limefriends.molde.entity.feed.MoldeFeedResponseInfoEntityList;
import com.limefriends.molde.entity.map.MoldeReportMapEntitiy;
import com.limefriends.molde.remote.MoldeNetwork;
import com.limefriends.molde.remote.MoldeRestfulService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoldeReportCardMapDialog extends BottomSheetDialogFragment {

    @BindView(R.id.report_history_list_view)
    RecyclerView report_history_list_view;

    private Context context;

    public MoldeReportCardMapDialogAdapter reportHistoryAdapter;
    //public ArrayList<MoldeReportMapEntitiy> reportHistoryList;
    private List<MoldeFeedEntity> feedList;
    public ReportCardItem reportCardData;

    public static MoldeReportCardMapDialog getInstance() {
        return new MoldeReportCardMapDialog();
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

//        reportHistoryList = new ArrayList<MoldeReportMapEntitiy>();
//        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
//        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
//        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
//        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));
//        reportHistoryList.add(new MoldeReportMapEntitiy("2018. 04. 16", "서울시 마포구 와우산로 92", "체육관 2층 화장실 3번째 칸"));

        //feedList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        report_history_list_view.setLayoutManager(layoutManager);
//        reportHistoryAdapter = new MoldeReportCardMapDialogAdapter(getContext(), reportHistoryList);
        reportHistoryAdapter = new MoldeReportCardMapDialogAdapter(getContext());
        report_history_list_view.setAdapter(reportHistoryAdapter);

        return view;
    }

    public void setData(ReportCardItem data) {

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<MoldeFeedResponseInfoEntityList> call
                = feedService.getFeedById(data.getRepId());

        call.enqueue(new Callback<MoldeFeedResponseInfoEntityList>() {
            @Override
            public void onResponse(Call<MoldeFeedResponseInfoEntityList> call, Response<MoldeFeedResponseInfoEntityList> response) {
                if (response.isSuccessful()) {
                    List<MoldeFeedEntity> entityList = fromSchemaToLocalEntity(response.body().getData());
                    reportHistoryAdapter.setData(entityList);
                }
            }

            @Override
            public void onFailure(Call<MoldeFeedResponseInfoEntityList> call, Throwable t) {

            }
        });

    }

    private List<MoldeFeedEntity> fromSchemaToLocalEntity(List<MoldeFeedResponseInfoEntity> entities) {
        List<MoldeFeedEntity> data = new ArrayList<>();
        for (MoldeFeedResponseInfoEntity entity : entities) {
            data.add(new MoldeFeedEntity(
                    entity.getRepId(),
                    entity.getUserName(),
                    entity.getUserEmail(),
                    entity.getUserId(),
                    entity.getRepContents(),
                    entity.getRepLat(),
                    entity.getRepLon(),
                    entity.getRepAddr(),
                    entity.getRepDetailAddr(),
                    entity.getRepDate(),
                    entity.getRepImg(),
                    entity.getRepState()
            ));
        }
        return data;
    }



}
