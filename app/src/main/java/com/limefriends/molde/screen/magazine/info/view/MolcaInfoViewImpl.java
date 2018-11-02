package com.limefriends.molde.screen.magazine.info.view;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.screen.common.recyclerview.adapter.RecyclerViewAdapter;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.views.BaseObservableView;
import com.limefriends.molde.screen.common.views.ViewFactory;

import java.util.List;

public class MolcaInfoViewImpl
        extends BaseObservableView<MolcaInfoView.Listener> implements MolcaInfoView {

    private FloatingActionButton btn_report_call;
    private NestedToolbar mNestedToolbar;
    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private ViewFactory mViewFactory;

    private RecyclerViewAdapter<MolcaInfo> mRecyclerViewAdapter;

    public MolcaInfoViewImpl(LayoutInflater inflater,
                             ViewGroup parent,
                             ViewFactory viewFactory) {
        this.mViewFactory = viewFactory;

        setRootView(inflater.inflate(R.layout.activity_info_prevent, parent, false));

        setupViews();
    }

    private void setupViews() {

        btn_report_call = findViewById(R.id.btn_location_report_call);
        mRecyclerView = findViewById(R.id.preventInfoList);
        mToolbar = findViewById(R.id.toolbar);

        setupToolbar();

        setupListeners();

        setupInfoList();
    }

    private void setupToolbar() {
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
    }

    private void setupListeners() {

        btn_report_call.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onReportClicked();
            }
        });

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });
    }

    private void setupInfoList() {

        mRecyclerViewAdapter = new RecyclerViewAdapter<>(mViewFactory);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void bindTitle(String title) {
        mNestedToolbar.setTitle(title);
    }

    @Override
    public void bindInfo(List<MolcaInfo> infoList) {
        mRecyclerViewAdapter.setData(infoList);
    }


}
