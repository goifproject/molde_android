package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.cardNewsComment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class CardNewsCommentItemViewImpl
        extends BaseObservableView<CardNewsCommentItemView.Listener>
        implements CardNewsCommentItemView {

    private ImageView img_comment_profile;
    private TextView txt_comment_user;
    private TextView txt_comment_date;
    private TextView txt_comment_content;
    private ToggleButton img_comment_siren;

    private int commentId;

    public CardNewsCommentItemViewImpl(LayoutInflater inflater,
                                       ViewGroup parent) {

        setRootView(inflater.inflate(R.layout.item_cardnews_comment, parent, false));

        setupViews();

        setupListener();
    }

    private void setupViews() {
        img_comment_profile = findViewById(R.id.comment_profile_image);
        txt_comment_user = findViewById(R.id.comment_user_name);
        txt_comment_date = findViewById(R.id.comment_date);
        txt_comment_content = findViewById(R.id.comment_content);
        img_comment_siren = findViewById(R.id.comment_report);
    }

    private void setupListener() {

        img_comment_siren.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(commentId);
            }
        });
    }

    @Override
    public void bindComment(CommentEntity commentEntity) {
        txt_comment_user.setText(commentEntity.getUserName());
        txt_comment_date.setText(DateUtil.fromLongToDate(commentEntity.getCommDate()));
        txt_comment_content.setText(commentEntity.getComment());
        commentId = commentEntity.getCommId();
        Log.e("호출확인", commentId+"");
    }
}
