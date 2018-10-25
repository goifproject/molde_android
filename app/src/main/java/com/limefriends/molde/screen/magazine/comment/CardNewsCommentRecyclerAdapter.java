package com.limefriends.molde.screen.magazine.comment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limefriends.molde.R;
import com.limefriends.molde.common.utils.DateUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.mypage.comment.MyCommentActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                .inflate(R.layout.item_cardnews_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(MagazineCommentViewHolder holder, int position) {
        CommentEntity cardNewsCommentEntity = dataList.get(position);
        holder.txt_comment_user.setText(cardNewsCommentEntity.getUserName());
        holder.txt_comment_date.setText(
                DateUtil.fromLongToDate(cardNewsCommentEntity.getCommDate()));
        holder.txt_comment_content.setText(cardNewsCommentEntity.getComment());
        holder.commentId = dataList.get(position).getCommId();
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

        int commentId;

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

                            AlertDialog dialog = new AlertDialog.Builder(context, R.style.DialogTheme)
                                    .setTitle(context.getText(R.string.dialog_report_comment_title))
                                    .setMessage(context.getText(R.string.dialog_report_comment_msg))
                                    .setPositiveButton(context.getText(R.string.yes), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (!view.isReporting()) {
                                                view.reportComment(commentId);
                                            }
                                        }
                                    })
                                    .setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                    });
        }
    }
}
