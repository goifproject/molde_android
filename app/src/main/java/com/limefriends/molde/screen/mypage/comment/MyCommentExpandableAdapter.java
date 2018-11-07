package com.limefriends.molde.screen.mypage.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.model.entity.cardNews.CardNewsEntity;

import java.util.ArrayList;
import java.util.List;

public class MyCommentExpandableAdapter extends BaseExpandableListAdapter {

    List<CardNewsEntity> newsEntityList = new ArrayList<>();

    private OnItemClickCallback callback;

    public interface OnItemClickCallback {
        void onParentItemClick(int groupPosition, int newsId);
        void onChildItemClick(int childPosition, int newsId, String description);
    }

    MyCommentExpandableAdapter(OnItemClickCallback callback) {
        this.callback = callback;
    }

    public void addAll(List<CardNewsEntity> newsEntityList) {
        this.newsEntityList = newsEntityList;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return newsEntityList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return newsEntityList.get(groupPosition).getComments().size();
    }

    @Override
    public CardNewsEntity getGroup(int groupPosition) {
        return newsEntityList.get(groupPosition);
    }

    @Override
    public CommentEntity getChild(int groupPosition, int childPosition) {
        return newsEntityList.get(groupPosition).getComments().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_comment_news, parent, false);

        TextView myComment_title = view.findViewById(R.id.myComment_title);

        ImageView myComment_image = view.findViewById(R.id.myComment_image);

        myComment_title.setText(newsEntityList.get(groupPosition).getDescription());
        if (newsEntityList.get(groupPosition).getNewsImg() != null && newsEntityList.get(groupPosition).getNewsImg().size() != 0) {
            Glide.with(parent.getContext()).load(newsEntityList.get(groupPosition).getNewsImg().get(0).getUrl()).into(myComment_image);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onParentItemClick(groupPosition, newsEntityList.get(groupPosition).getNewsId());
            }
        });

        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_comment_comment, parent, false);

        TextView myComment_date = view.findViewById(R.id.myComment_date);

        TextView myComment_comment = view.findViewById(R.id.myComment_comment);



        CommentEntity comment = newsEntityList.get(groupPosition).getComments().get(childPosition);

        myComment_date.setText(DateUtil.fromLongToDate(comment.getCommDate()));
        myComment_comment.setText(comment.getComment());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onChildItemClick(childPosition, newsEntityList.get(groupPosition).getNewsId(), newsEntityList.get(groupPosition).getDescription());
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
