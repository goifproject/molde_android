package com.limefriends.molde.screen.map.main.reportCard;

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
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.common.utils.NetworkUtil;
import com.limefriends.molde.model.repository.FromSchemaToEntity;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.networking.schema.feed.FeedResponseSchema;
import com.limefriends.molde.networking.MoldeNetwork;
import com.limefriends.molde.networking.service.MoldeRestfulService;

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

    private ApplyReportCardInfoCallback mCallback;

    public interface ApplyReportCardInfoCallback {

        void fetchReportCardInfo(int reportCardId);
    }

    public void setCallback(ApplyReportCardInfoCallback callback) {
        mCallback = callback;
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

        View view = inflater.inflate(R.layout.dialog_feed_list, container,false);
        ButterKnife.bind(this, view);

        report_history_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        reportHistoryAdapter = new MapReportCardListDialogAdapter(getContext());
        report_history_list_view.setAdapter(reportHistoryAdapter);

        return view;
    }



//    public void setData(List<FeedEntity> data) {
//        reportHistoryAdapter.setData(data);
//    }

    public void setData(int reportId, Context context) {

        if (!NetworkUtil.isConnected(context)) {
            Toast.makeText(context, "인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
            return;
        }

        MoldeRestfulService.Feed feedService
                = MoldeNetwork.getInstance().generateService(MoldeRestfulService.Feed.class);

        Call<FeedResponseSchema> call = feedService.getFeedById(reportId);

        call.enqueue(new Callback<FeedResponseSchema>() {
            @Override
            public void onResponse(Call<FeedResponseSchema> call, Response<FeedResponseSchema> response) {
                if (response.isSuccessful()) {
                    List<FeedEntity> entityList = FromSchemaToEntity.feed(response.body().getData());
                    reportHistoryAdapter.setData(entityList);
                }
            }

            @Override
            public void onFailure(Call<FeedResponseSchema> call, Throwable t) {

            }
        });

    }





}
