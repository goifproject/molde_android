package com.limefriends.molde.screen.common.dialog.view;

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
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.model.repository.Repository;
import com.limefriends.molde.screen.common.recyclerviewHelper.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.ItemViewType;
import com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportCard.ReportCardItemView;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseObservableBottomSheetDialog;

import io.reactivex.disposables.CompositeDisposable;

import static com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportCard.ReportCardItemView.*;

public class ReportCardListDialog extends BaseObservableBottomSheetDialog<Listener> {

    public interface OnReportCardItemClickListener {

        void onReportCardItemClicked(int item);
    }

    /* package */ public static final String ARG_REPORT_ID = "ARG_REPORT_ID";

    private RecyclerView report_history_list_view;
    private RecyclerViewAdapter<FeedEntity> mFeedAdapter;

    @Service private Repository.Feed mFeedRepository;
    @Service private ViewFactory mViewFactory;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getInjector().inject(this);

        View view = setupViews(inflater, container);

        setData(getArguments().getInt(ARG_REPORT_ID, 0));

        return view;
    }

    private View setupViews(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_feed_list, container,false);
        report_history_list_view = view.findViewById(R.id.report_history_list_view);
        report_history_list_view.setLayoutManager(new LinearLayoutManager(getContext()));
        mFeedAdapter = new RecyclerViewAdapter<>(mViewFactory, ItemViewType.REPORT_CARD);
        mFeedAdapter.setOnItemClickListener(itemId -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(itemId);
            }
        });
        report_history_list_view.setAdapter(mFeedAdapter);
        return view;
    }

    public void setData(int reportId) {
        mCompositeDisposable.add(
            mFeedRepository
                    .getFeedById(reportId)
                    .subscribe(
                            entities -> mFeedAdapter.addData(entities),
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
