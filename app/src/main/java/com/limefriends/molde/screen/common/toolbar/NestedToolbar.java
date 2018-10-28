package com.limefriends.molde.screen.common.toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.views.BaseView;

public class NestedToolbar extends BaseView {

    public interface NavigateUpClickListener {
        void onNavigateUpClicked();
    }

    public interface CompleteClickListener {
        void onHamburgerClicked();
    }

    private final TextView mToolbarTitle;
    private final ImageView mToolbarBtnBack;
    private final Button mToolbarBtnComplete;

    private NavigateUpClickListener mNavigateUpClickListener;
    private CompleteClickListener mCompleteClickListener;

    public NestedToolbar(LayoutInflater inflater, ViewGroup parent) {

        setRootView(inflater.inflate(R.layout.element_toolbar_layout, parent, false));

        mToolbarTitle = findViewById(R.id.toolbar_title);
        mToolbarBtnBack = findViewById(R.id.toolbar_btn_back);
        mToolbarBtnComplete = findViewById(R.id.toolbar_btn_done);

        setupListener();
    }

    private void setupListener() {

        mToolbarBtnBack.setOnClickListener(v -> mNavigateUpClickListener.onNavigateUpClicked());

        mToolbarBtnComplete.setOnClickListener(v -> mCompleteClickListener.onHamburgerClicked());
    }

    public void setTitle(String title) {
        mToolbarTitle.setText(title);
    }

    public void enableUpButtonAndListen(NavigateUpClickListener listener) {
        mToolbarBtnBack.setVisibility(View.VISIBLE);
        this.mNavigateUpClickListener = listener;
    }

    public void enableCompleteButtonAndListen(CompleteClickListener listener) {
        mToolbarBtnComplete.setVisibility(View.VISIBLE);
        this.mCompleteClickListener = listener;
    }

}
