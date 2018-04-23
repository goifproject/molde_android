package com.limefriends.molde.menu_magazine;

import android.content.Context;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by joo on 2018. 4. 19..
 */


class CommentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.img_comment_profile)
    ImageView img_comment_profile;

    @BindView(R.id.txt_comment_user)
    TextView txt_comment_user;

    @BindView(R.id.txt_comment_date)
    TextView txt_comment_date;

    @BindView(R.id.txt_comment_content)
    TextView txt_comment_content;

    @BindView(R.id.img_comment_siren)
    ImageView img_comment_siren;


    public CommentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}


public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentViewHolder> implements View.OnClickListener {

    private Context context;
    private int resourceId;
    private List<CommentListElement> dataList;
    private View view;

    // resourceId == R.layout.magazine_item_comment_recycler
    // 즉 하나의 단위가 되는 layout 의미
    public CommentRecyclerAdapter(Context context, int resourceId, List<CommentListElement> dataList, View view) {
        this.context = context;
        this.resourceId = resourceId;
        this.dataList = dataList;
        this.view = view;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(resourceId, parent, false));
    }


    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        CommentListElement commentListElement = dataList.get(position);
        Glide.with(context).load(commentListElement.getProfileImg())
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.img_comment_profile);
        holder.txt_comment_user.setText(commentListElement.getUserName());
        holder.txt_comment_date.setText(commentListElement.getCreDate());
        holder.txt_comment_content.setText(commentListElement.getContent());
        holder.img_comment_siren.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Snackbar.make(view, "댓글이 신고되었습니다.", 300).show();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
