package com.limefriends.molde.screen.view.magazine.info;

import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface MolcaInfoView extends ObservableView<MolcaInfoView.Listener> {

    public interface Listener {

        void onReportClicked();

        void onNavigateUpClicked();
    }

    void bindTitle(String title);

    void bindInfo(List<MolcaInfo> infoList);
}
