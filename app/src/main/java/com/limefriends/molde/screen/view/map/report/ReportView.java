package com.limefriends.molde.screen.view.map.report;

import android.net.Uri;
import android.support.v4.util.SparseArrayCompat;

import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface ReportView extends ObservableView<ReportView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onSelectPictureClicked(int sequence, int size);

        void onFindLocationClicked();

        void onSendReportClicked(SparseArrayCompat<Uri> images, String reportContent,
                                 String reportAddress, String detailAddress, String email, boolean isGreenZone);

        void onCancelReportClicked();

        void onGreenZoneCheckChanged(boolean isChecked);
    }

    void onBackPressed();

    void showSnackBar(String message);

    void bindAuthority(int authority);

    void switchGreenZone();

    void showProgressIndication();

    void hideProgressIndication();

    void bindAddress(String address);

    void setPictureForReport(Uri uri, int seq);

    void setPictureForReport(List<String> uri, int seq);



}
