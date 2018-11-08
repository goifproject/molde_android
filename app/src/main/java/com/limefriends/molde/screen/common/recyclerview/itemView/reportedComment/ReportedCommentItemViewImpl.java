package com.limefriends.molde.screen.common.recyclerview.itemView.reportedComment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.comment.CommentEntity;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class ReportedCommentItemViewImpl
        extends BaseObservableView<ReportedCommentItemView.Listener> implements ReportedCommentItemView {

    RelativeLayout cardnews_comment_container;
    ImageView img_comment_profile;
    TextView txt_comment_user;
    TextView txt_comment_date;
    TextView txt_comment_content;
    ToggleButton img_comment_siren;

    private int position;
    private ImageLoader mImageLoader;

    public ReportedCommentItemViewImpl(LayoutInflater inflater,
                                       ViewGroup parent,
                                       ImageLoader imageLoader) {

        this.mImageLoader = imageLoader;

        setRootView(inflater.inflate(R.layout.item_cardnews_comment, parent, false));

        setupViews();

        setupListener();
    }

    private void setupViews() {
        cardnews_comment_container = findViewById(R.id.cardnews_comment_container);
        img_comment_profile = findViewById(R.id.comment_profile_image);
        txt_comment_date = findViewById(R.id.comment_user_name);
        txt_comment_content = findViewById(R.id.comment_content);
        img_comment_siren = findViewById(R.id.comment_report);
    }

    private void setupListener() {
        
        cardnews_comment_container.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(position);
            }
        });

        img_comment_siren.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (Listener listener : getListeners()) {
                listener.onItem2Clicked(position);
            }
        });
    }


    @Override
    public void bindComment(CommentEntity entity, int position) {
        this.position = position;
        mImageLoader.load(R.drawable.molde_logo, img_comment_profile);
        txt_comment_user.setText(entity.getUserName());
        txt_comment_date.setText(DateUtil.fromLongToDate(entity.getCommDate()));
        txt_comment_content.setText(entity.getComment());
        img_comment_siren.setBackgroundResource(R.drawable.ic_comment_siren_on);
    }
}
