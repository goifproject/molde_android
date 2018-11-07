package com.limefriends.molde.screen.mypage.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ReportedCommentAdapter
        extends RecyclerView.Adapter<ReportedCommentAdapter.ViewHolder> {

    public interface OnCommentClickListener {
        void onSirenClicked(int position, int commentId);

        void onCommentClicked(int cardNewsId);
    }

    private Context context;
    private List<CommentEntity> dataList = new ArrayList<>();

    private OnCommentClickListener mOnCommentClickListener;

    ReportedCommentAdapter(Context context, OnCommentClickListener onCommentClickListener) {
        this.context = context;
        this.mOnCommentClickListener = onCommentClickListener;
    }

    public void addData(List<CommentEntity> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void deleteComment(int position) {
        dataList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public ReportedCommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReportedCommentAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_cardnews_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(ReportedCommentAdapter.ViewHolder holder, int position) {
        CommentEntity cardNewsCommentEntity = dataList.get(position);
        Glide.with(context).load(R.drawable.molde_logo)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.img_comment_profile);
        holder.txt_comment_user.setText(cardNewsCommentEntity.getUserName());
        holder.txt_comment_date.setText(
                DateUtil.fromLongToDate(cardNewsCommentEntity.getCommDate()));
        holder.txt_comment_content.setText(cardNewsCommentEntity.getComment());
        holder.commentId = dataList.get(position).getCommId();
        holder.img_comment_siren.setBackgroundResource(R.drawable.ic_comment_siren_on);
        holder.position = position;
        holder.cardNewsId = cardNewsCommentEntity.getNewsId();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cardnews_comment_container)
        RelativeLayout cardnews_comment_container;
        @BindView(R.id.comment_profile_image)
        ImageView img_comment_profile;
        @BindView(R.id.comment_user_name)
        TextView txt_comment_user;
        @BindView(R.id.comment_date)
        TextView txt_comment_date;
        @BindView(R.id.comment_content)
        TextView txt_comment_content;
        @BindView(R.id.comment_report)
        ToggleButton img_comment_siren;

        int commentId;
        int position;
        int cardNewsId;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {
            img_comment_siren
                    .setOnCheckedChangeListener((buttonView, isChecked)
                            -> mOnCommentClickListener.onSirenClicked(position, commentId));

            cardnews_comment_container.setOnClickListener(v
                    -> mOnCommentClickListener.onCommentClicked(cardNewsId));
        }

    }
}
