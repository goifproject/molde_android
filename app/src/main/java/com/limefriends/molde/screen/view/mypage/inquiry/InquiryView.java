package com.limefriends.molde.screen.view.mypage.inquiry;

import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

import java.util.List;

public interface InquiryView extends ObservableView<InquiryView.Listener> {

    public interface Listener {

        void onNavigateUpClicked();

        void onSubmitInquireClicked(String content, String email);

        void onInquireClicked(int itemId);
    }

    void showProgressIndication();

    void hideProgressIndication();

    void bindAuthority(long authority);

    void bindInquiry(List<FaqEntity> entityList);
}
