package com.limefriends.molde.screen.common.recyclerview.itemView;

import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.screen.common.view.ObservableView;

public interface InquiryItemView extends ObservableView<InquiryItemView.Listener> {

    public interface Listener {

        void onItemClicked(int itemId);
    }

    void bindInquiry(FaqEntity entity);

}
