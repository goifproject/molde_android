package com.limefriends.molde.screen.common.toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.view.BaseView;

public class NestedToolbar extends BaseView {

    public interface NavigateUpClickListener {
        void onNavigateUpClicked();
    }

    public interface CompleteClickListener {
        void onHamburgerClicked();
    }

    public interface SwitchGreenZoneListener {
        void onSwitchGreenZoneClicked(boolean isChecked);
    }

    private final TextView mToolbarTitle;
    private final ImageView mToolbarBtnBack;
    private final Button mToolbarBtnComplete;
    private final Switch mSwitchGreenZone;

    private NavigateUpClickListener mNavigateUpClickListener;
    private CompleteClickListener mCompleteClickListener;
    private SwitchGreenZoneListener mSwitchGreenZoneListener;

    public NestedToolbar(LayoutInflater inflater, ViewGroup parent) {

        setRootView(inflater.inflate(R.layout.element_toolbar_layout, parent, false));

        mToolbarTitle = findViewById(R.id.toolbar_title);
        mToolbarBtnBack = findViewById(R.id.toolbar_btn_back);
        mToolbarBtnComplete = findViewById(R.id.toolbar_btn_done);
        mSwitchGreenZone = findViewById(R.id.toolbar_switch_green_zone);

        setupListener();
    }

    private void setupListener() {

        mToolbarBtnBack.setOnClickListener(v -> mNavigateUpClickListener.onNavigateUpClicked());

        mToolbarBtnComplete.setOnClickListener(v -> mCompleteClickListener.onHamburgerClicked());

        mSwitchGreenZone.setOnCheckedChangeListener((buttonView, isChecked)
                -> mSwitchGreenZoneListener.onSwitchGreenZoneClicked(isChecked));
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

    public void enableSwitchButtonAndListen(SwitchGreenZoneListener listener) {
        mSwitchGreenZone.setVisibility(View.VISIBLE);
        this.mSwitchGreenZoneListener = listener;
    }

}
