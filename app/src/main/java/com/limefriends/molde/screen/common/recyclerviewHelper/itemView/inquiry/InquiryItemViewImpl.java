package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.inquiry;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.model.entity.faq.FaqEntity;
import com.limefriends.molde.screen.common.view.BaseObservableView;


public class InquiryItemViewImpl
        extends BaseObservableView<InquiryItemView.Listener> implements InquiryItemView {

    private TextView inquiry_content;
    private TextView inquiry_email;
    private FaqEntity mFaqEntity;

    public InquiryItemViewImpl(LayoutInflater inflater,
                           ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.item_inquiry, parent, false));

        setupViews();

        setupListener();
    }

    private void setupViews() {
        inquiry_content = findViewById(R.id.inquiry_content);
        inquiry_email = findViewById(R.id.inquiry_email);
    }

    private void setupListener() {
        inquiry_content.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(mFaqEntity.getFaqId());
            }
        });
    }

    @Override
    public void bindInquiry(FaqEntity entity) {
        this.mFaqEntity = entity;
        inquiry_content.setText(entity.getFaqContents());
        inquiry_email.setText(entity.getFaqEmail());
    }
}
