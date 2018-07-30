package com.limefriends.molde.menu_magazine.comment;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_magazine.entity.CardNewsCommentEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
    ImageView img_comment_siren;


    public MagazineCommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}


public class MagazineCommentRecyclerAdapter extends RecyclerView.Adapter<MagazineCommentViewHolder> implements View.OnClickListener {

    private Context context;
    private int resourceId;
    private List<CardNewsCommentEntity> dataList;
    private View view;

    // resourceId == R.layout.magazine_comment_item
    // 즉 하나의 단위가 되는 layout 의미
    public MagazineCommentRecyclerAdapter(Context context, int resourceId, List<CardNewsCommentEntity> dataList, View view) {
        this.context = context;
        this.resourceId = resourceId;
        this.dataList = dataList;
        this.view = view;
    }

    @Override
    public MagazineCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MagazineCommentViewHolder(LayoutInflater.from(context).inflate(resourceId, parent, false));
    }

    @Override
    public void onBindViewHolder(MagazineCommentViewHolder holder, int position) {
        CardNewsCommentEntity cardNewsCommentEntity = dataList.get(position);
        Glide.with(context).load(cardNewsCommentEntity.getProfileImg())
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.img_comment_profile);
        holder.txt_comment_user.setText(cardNewsCommentEntity.getUserName());
        holder.txt_comment_date.setText(cardNewsCommentEntity.getCreDate());
        holder.txt_comment_content.setText(cardNewsCommentEntity.getContent());
        holder.img_comment_siren.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        ImageView img_comment_siren = v.findViewById(R.id.comment_report);
        img_comment_siren.setImageResource(R.drawable.ic_siren_true);
        // TODO 신고 <-> 신고 취소 toggle
        Snackbar.make(view, "댓글이 신고되었습니다.", 300).show();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
