package com.limefriends.molde.ui.mypage.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.limefriends.molde.R;
import com.limefriends.molde.entity.comment.CommentEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-05-19.
 */
public class MyCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int resourceId;
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    public MyCommentAdapter(Context context, int resourceId) {
        this.context = context;
        this.resourceId = resourceId;
       // this.commentEntityList = commentEntityList;
    }

    public void setData(List<CommentEntity> data) {
        this.commentEntityList = data;
        notifyDataSetChanged();
    }

    public class MyPageMyCommentViewHorder extends RecyclerView.ViewHolder {
        @BindView(R.id.myComment_title)
        TextView myComment_title;

        @BindView(R.id.myComment_image)
        ImageView myComment_image;

        @BindView(R.id.myComment_date)
        TextView myComment_date;

        @BindView(R.id.myComment_comment)
        TextView myComment_comment;

        @BindView(R.id.myComment_menu)
        ImageView myComment_menu;

        public MyPageMyCommentViewHorder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public MyPageMyCommentViewHorder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mypage_my_comment_comment_item, parent, false);
        return new MyPageMyCommentViewHorder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MyPageMyCommentViewHorder viewHorder = (MyPageMyCommentViewHorder) holder;

        // feed summary
//        viewHorder.myComment_title.setText(commentEntityList.get(position).getMyComment_title());
//        Glide.with(context).load(commentEntityList.get(position).getMyComment_image())
//                .into(viewHorder.myComment_image);
        // comment
        viewHorder.myComment_comment.setText(commentEntityList.get(position).getComment());
        viewHorder.myComment_date.setText(commentEntityList.get(position).getCommDate());

        viewHorder.myComment_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, viewHorder.myComment_menu);
                popup.inflate(R.menu.my_comment_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.commentSee:
                                Toast.makeText(context,"본문 보기", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.commentEdit:
                                Toast.makeText(context,"댓글 수정", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.commentDel:
                                Toast.makeText(context,"댓글 삭제", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentEntityList.size();
    }
}
