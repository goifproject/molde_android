package com.limefriends.molde.screen.common.recyclerview.itemView.myFeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.view.BaseObservableView;

import static com.limefriends.molde.common.Constant.ReportState.ACCEPTED;
import static com.limefriends.molde.common.Constant.ReportState.CLEAN;
import static com.limefriends.molde.common.Constant.ReportState.DENIED;
import static com.limefriends.molde.common.Constant.ReportState.FOUND;
import static com.limefriends.molde.common.Constant.ReportState.RECEIVING;

public class MyFeedItemViewImpl
        extends BaseObservableView<MyFeedItemView.Listener> implements MyFeedItemView {

    private RelativeLayout mypage_report_layout;
    private ImageView mypage_report_map;
    private TextView mypage_report_date;
    private TextView mypage_report_location;
    private TextView mypage_report_address;

    private View report_progress_line_first;
    private View report_progress_line_second;
    private ImageView report_progress_dot_first;
    private ImageView report_progress_dot_second_yellow;
    private ImageView report_progress_dot_third_yellow;

    private TextView report_progress_text_accepted;
    private TextView report_progress_text_completed;

    private FeedEntity mFeedEntity;
    private ImageLoader mImageLoader;
    private int position;

    public MyFeedItemViewImpl(LayoutInflater inflater,
                              ViewGroup parent,
                              ImageLoader imageLoader) {

        this.mImageLoader = imageLoader;

        setRootView(inflater.inflate(R.layout.item_my_feed, parent, false));

        setupViews();
        
        setupListener();
    }
    
    private void setupViews() {
        mypage_report_layout = findViewById(R.id.mypage_report_layout);
        mypage_report_map = findViewById(R.id.mypage_report_map);
        mypage_report_date = findViewById(R.id.mypage_report_date);
        mypage_report_location = findViewById(R.id.mypage_report_location);
        mypage_report_address = findViewById(R.id.mypage_report_address);

        report_progress_line_first = findViewById(R.id.report_progress_line_first);
        report_progress_line_second = findViewById(R.id.report_progress_line_second);
        report_progress_dot_first = findViewById(R.id.report_progress_dot_first);
        report_progress_dot_second_yellow = findViewById(R.id.report_progress_dot_second_yellow);
        report_progress_dot_third_yellow = findViewById(R.id.report_progress_dot_third_yellow);

        report_progress_text_accepted = findViewById(R.id.report_progress_text_accepted);
        report_progress_text_completed = findViewById(R.id.report_progress_text_completed);
    }

    private void setupListener() {
        mypage_report_layout.setOnClickListener(v -> {
            // listener.OnItemClick(feedEntities.get(position).getRepId(), position)
            for (Listener listener : getListeners()) {
                listener.onItemClicked(mFeedEntity.getRepId());
            }
        });
    }


    @Override
    public void bindFeed(FeedEntity entity, int position) {
        this.position = position;
        this.mFeedEntity = entity;
        if (entity.getRepImg() != null && entity.getRepImg().size() != 0) {
            mImageLoader.load(entity.getRepImg().get(0).getFilepath(),
                    R.drawable.img_placeholder_my_feed, mypage_report_map);
        } else {
            mImageLoader.load(R.drawable.img_placeholder_my_feed, mypage_report_map);
        }
        switch (entity.getRepState()) {
            case RECEIVING:
                report_progress_dot_second_yellow
                        .setVisibility(View.INVISIBLE);
                report_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                report_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
                report_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
                report_progress_text_accepted
                        .setTextColor(getContext().getResources().getColor(R.color.colorDivision));
                report_progress_text_completed
                        .setTextColor(getContext().getResources().getColor(R.color.colorDivision));
                break;
            case ACCEPTED:
                report_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                report_progress_dot_third_yellow
                        .setVisibility(View.INVISIBLE);
                report_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                report_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorDivision));
                report_progress_text_accepted
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                report_progress_text_completed
                        .setTextColor(getContext().getResources().getColor(R.color.colorDivision));
                break;
            case FOUND:
            case CLEAN:
                report_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                report_progress_dot_third_yellow
                        .setVisibility(View.VISIBLE);
                report_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                report_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                report_progress_text_accepted
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                report_progress_text_completed
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                break;
            case DENIED:
                report_progress_dot_second_yellow
                        .setVisibility(View.VISIBLE);
                report_progress_line_first.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                report_progress_line_second.setBackgroundColor(
                        getContext().getResources().getColor(R.color.colorAccent));
                report_progress_text_accepted
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                report_progress_text_completed
                        .setTextColor(getContext().getResources().getColor(R.color.colorInfoTextColor));
                break;
        }
        mypage_report_date.setText(DateUtil.fromLongToDate(entity.getRepDate()));
        mypage_report_location.setText(entity.getRepAddr());
        mypage_report_address.setText(entity.getRepDetailAddr());
    }
}
