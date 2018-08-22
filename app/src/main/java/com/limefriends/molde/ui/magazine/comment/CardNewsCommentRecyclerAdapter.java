package com.limefriends.molde.ui.magazine.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.comment.CommentEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class CardNewsCommentRecyclerAdapter
        extends RecyclerView.Adapter<CardNewsCommentRecyclerAdapter.MagazineCommentViewHolder> {

    private Context context;
    private List<CommentEntity> dataList = new ArrayList<>();
    private CardNewsCommentActivity view;

    CardNewsCommentRecyclerAdapter(Context context, CardNewsCommentActivity view) {
        this.context = context;
        this.view = view;
    }

    public void addData(CommentEntity data) {
        dataList.add(data);
        notifyDataSetChanged();
    }

    public void addData(List<CommentEntity> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public MagazineCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MagazineCommentViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.magazine_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MagazineCommentViewHolder holder, int position) {
        CommentEntity cardNewsCommentEntity = dataList.get(position);
        Glide.with(context).load(R.drawable.img_dummy_profile)
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.img_comment_profile);
        holder.txt_comment_user.setText(cardNewsCommentEntity.getUserName());
        holder.txt_comment_date.setText(cardNewsCommentEntity.getCommDate());
        holder.txt_comment_content.setText(cardNewsCommentEntity.getComment());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MagazineCommentViewHolder extends RecyclerView.ViewHolder {

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

        MagazineCommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setupListener();
        }

        private void setupListener() {
            img_comment_siren
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                img_comment_siren.setBackgroundResource(R.drawable.ic_siren_true);
                                view.showSnack("댓글이 신고되었습니다.");
                            } else {
                                img_comment_siren.setBackgroundResource(R.drawable.ic_siren);
                                view.showSnack("댓글이 신고가 취소되었습니다.");
                            }
                        }
                    });
        }

    }
}
