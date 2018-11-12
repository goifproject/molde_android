package com.limefriends.molde.screen.common.recyclerviewHelper.itemView.reportCard;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class ReportCardItemViewImpl
        extends BaseObservableView<ReportCardItemView.Listener> implements ReportCardItemView {

    private RelativeLayout report_info_layout;
    private ImageView report_info_thumbnail_image;
    private TextView report_info_date;
    private TextView report_info_address;
    private TextView report_info_detail_address;

    private ImageLoader mImageLoader;
    private int feedId;

    public ReportCardItemViewImpl(LayoutInflater inflater,
                                  ViewGroup parent,
                                  ImageLoader imageLoader) {
        this.mImageLoader = imageLoader;

        setRootView(inflater.inflate(R.layout.item_feed_dialog, parent, false));

        setupViews();

        setupListener();
    }

    private void setupViews() {
        report_info_layout = findViewById(R.id.report_info_layout);
        report_info_thumbnail_image = findViewById(R.id.report_info_thumbnail_image);
        report_info_date = findViewById(R.id.report_info_date);
        report_info_address = findViewById(R.id.report_info_address);
        report_info_detail_address = findViewById(R.id.report_info_detail_address);
    }

    private void setupListener() {
        report_info_layout.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(feedId);
            }
        });
    }

    @Override
    public void bindFeed(FeedEntity entity) {
        this.feedId = entity.getRepId();
        report_info_date.setText(DateUtil.fromLongToDate(entity.getRepDate()));
        report_info_address.setText(entity.getRepAddr());
        report_info_detail_address.setText(entity.getRepDetailAddr());
        if (entity.getRepImg() != null && entity.getRepImg().size() != 0) {
            mImageLoader.loadCenterCrop(
                    entity.getRepImg().get(0).getFilepath(),
                    R.drawable.img_placeholder_magazine,
                    report_info_thumbnail_image);
        } else {
            mImageLoader.load(R.drawable.img_placeholder_magazine, report_info_thumbnail_image);
        }
    }
}
