package com.limefriends.molde.ui.mypage.inquiry;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.entity.faq.FaqEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InquiryAdapter extends RecyclerView.Adapter<InquiryAdapter.InquiryViewHolder> {

    private List<FaqEntity> faqEntities = new ArrayList<>();
    private String sender;


    public void addAll(List<FaqEntity> entities) {
        faqEntities.addAll(entities);
        notifyDataSetChanged();
    }

    public InquiryAdapter(String sender) {
        this.sender = sender;
    }

    @Override
    public InquiryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inquiry, parent, false);
        return new InquiryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InquiryViewHolder holder, int position) {
        FaqEntity entity = faqEntities.get(position);
        holder.inquiry_content.setText(entity.getFaqContents());
        holder.inquiry_email.setText(entity.getFaqEmail());
        holder.receiver = entity.getFaqEmail();
        holder.content = entity.getFaqContents();
    }

    @Override
    public int getItemCount() {
        return faqEntities.size();
    }

    void send(String sender, String receiver, String content, Context context) {
        String[] tos = {receiver};
        String[] me = {sender};
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);   // 다중으로 보내고 싶을 때 사용
        intent.putExtra(Intent.EXTRA_EMAIL, tos);   // 받는 사람. 꼭 배열에 넣어줘야 한다. 아마 다수의 사람에게 보낼 수 있는 듯
        intent.putExtra(Intent.EXTRA_CC, me);       // 참조. 뭐하는 것인지는 아직 확실하지 않음.
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("message/rfc822");           // 이걸로 하면 선택할 앱이 몇 개 덜 뜸.
        context.startActivity(Intent.createChooser(intent, "선택"));
    }

    class InquiryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.inquiry_content)
        TextView inquiry_content;
        @BindView(R.id.inquiry_email)
        TextView inquiry_email;

        String receiver;
        String content;

        InquiryViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            inquiry_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send(sender, receiver, content, v.getContext());
                }
            });
        }
    }
}
