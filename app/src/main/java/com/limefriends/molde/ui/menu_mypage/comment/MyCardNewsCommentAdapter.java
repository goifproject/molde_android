package com.limefriends.molde.ui.menu_mypage.comment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.entity.comment.MoldeCommentEntity;
import com.limefriends.molde.entity.news.MoldeCardNewsEntity;
import com.limefriends.molde.ui.menu_magazine.comment.MagazineCommentActivity;
import com.limefriends.molde.ui.menu_magazine.detail.MagazineCardnewsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 2018-05-19.
 */
public class MyCardNewsCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CARD_NEWS = 0;
    public static final int COMMENT = 1;
    private Context context;
    private int resourceId;
    // private List<MoldeCommentEntity> myPageMyCommentEntityList;
    private List myCommentList = new ArrayList();

    public MyCardNewsCommentAdapter(Context context) {
        this.context = context;
        this.resourceId = resourceId;
        // this.myPageMyCommentEntityList = myPageMyCommentEntityList;
    }

    public void addAll(List data) {
        myCommentList.addAll(data);
        notifyItemRangeInserted(0, data.size()-1);
        // notifyDataSetChanged();
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder  {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class CommentViewHolder extends BaseViewHolder {

        @BindView(R.id.myComment_date)
        TextView myComment_date;

        @BindView(R.id.myComment_comment)
        TextView myComment_comment;

        @BindView(R.id.myComment_menu)
        ImageView myComment_menu;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class CardNewsViewHolder extends BaseViewHolder {
        @BindView(R.id.myComment_title)
        TextView myComment_title;

        @BindView(R.id.myComment_image)
        ImageView myComment_image;

        public CardNewsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (myCommentList.get(position) instanceof MoldeCardNewsEntity) {
            return CARD_NEWS;
        } else if (myCommentList.get(position) instanceof MoldeCommentEntity) {
            return COMMENT;
        }
        return -1;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case CARD_NEWS:
                view = LayoutInflater.from(context).inflate(R.layout.mypage_my_comment_cardnews_item, parent, false);
                return new CardNewsViewHolder(view);
            case COMMENT:
                view = LayoutInflater.from(context).inflate(R.layout.mypage_my_comment_comment_item, parent, false);
                return new CommentViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof CardNewsViewHolder) {
            final MoldeCardNewsEntity entity = (MoldeCardNewsEntity) myCommentList.get(position);
            ((CardNewsViewHolder) holder).myComment_title.setText(entity.getDescription());
            if (entity.getNewsImg().size() != 0) {
                Glide.with(context).load(entity.getNewsImg().get(0).getUrl()).placeholder(R.drawable.img_cardnews_dummy).into(((CardNewsViewHolder) holder).myComment_image);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MagazineCardnewsDetailActivity.class);
                    intent.putExtra("cardNewsId", entity.getNewsId());
                    context.startActivity(intent);
                }
            });
        } else if (holder instanceof CommentViewHolder) {
            final MoldeCommentEntity entity = (MoldeCommentEntity) myCommentList.get(position);
            ((CommentViewHolder) holder).myComment_comment.setText(entity.getComment());
            ((CommentViewHolder) holder).myComment_date.setText(entity.getCommDate());
            ((CommentViewHolder) holder).myComment_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, ((CommentViewHolder) holder).myComment_menu);
                    popup.inflate(R.menu.my_comment_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.commentSee:
                                    Toast.makeText(context, "본문 보기", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.commentEdit:
                                    Toast.makeText(context, "댓글 수정", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.commentDel:
                                    Toast.makeText(context, "댓글 삭제", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MagazineCommentActivity.class);
                    intent.putExtra("cardNewsId", entity.getNewsId());
                    intent.putExtra("description", entity.getComment());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return myCommentList.size();
    }
}
