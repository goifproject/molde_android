package com.limefriends.molde.screen.map.main.feedDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.controller.BaseBottomSheetDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

public class MapReportCardListDialog extends BaseBottomSheetDialog {

    @BindView(R.id.report_history_list_view)
    RecyclerView report_history_list_view;

    @Service private Repository.Feed mFeedRepository;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private MapReportCardListDialogAdapter reportHistoryAdapter;

    public static MapReportCardListDialog newInstance(int reportId) {
        MapReportCardListDialog dialog = new MapReportCardListDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("reportId", reportId);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getInjector().inject(this);



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

    @Override
    public void onStart() {
        super.onStart();

        int reportId = getArguments().getInt("reportId");

        setData(reportId);
    }

    public void setData(int reportId) {

        mCompositeDisposable.add(
            mFeedRepository
                    .getFeedById(reportId)
                    .subscribe(
                            entities -> reportHistoryAdapter.setData(entities),
                            err -> {},
                            () -> {}
                    )
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeDisposable.clear();
    }
}
