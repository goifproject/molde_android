package com.limefriends.molde.screen.common.recyclerview.itemView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.util.DateUtil;
import com.limefriends.molde.model.entity.feed.FeedEntity;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.ImageDialog;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.view.BaseObservableView;

import static com.limefriends.molde.common.Constant.ReportState.ACCEPTED;
import static com.limefriends.molde.common.Constant.ReportState.CLEAN;
import static com.limefriends.molde.common.Constant.ReportState.FOUND;
import static com.limefriends.molde.common.Constant.ReportState.RECEIVING;

public class FeedItemViewImpl
        extends BaseObservableView<FeedItemView.Listener> implements FeedItemView {

    public static final String SHOW_FEED_IMAGE_DIALOG = "SHOW_FEED_IMAGE_DIALOG";
    private RelativeLayout feed_content;
    private ImageView feed_image;
    private TextView feed_address;
    private TextView feed_detail_address;
    private TextView feed_date;
    private ImageView feed_state;

    private ImageLoader mImageLoader;
    private DialogFactory mDialogFactory;
    private DialogManager mDialogManager;
    private FeedEntity mEntity;
    private int feedId;

    public FeedItemViewImpl(LayoutInflater inflater,
                            ViewGroup parent,
                            ImageLoader imageLoader,
                            DialogFactory dialogFactory,
                            DialogManager dialogManager) {

        setRootView(inflater.inflate(R.layout.item_feed, parent, false));

        this.mImageLoader = imageLoader;
        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;

        setupViews();

        setupListener();
    }

    private void setupViews() {
        feed_content = findViewById(R.id.feed_content);
        feed_image = findViewById(R.id.feed_image);
        feed_address = findViewById(R.id.feed_address);
        feed_detail_address = findViewById(R.id.feed_detail_address);
        feed_date = findViewById(R.id.feed_date);
        feed_state = findViewById(R.id.feed_state);

        feed_image.setClipToOutline(true);
        feed_image.setElevation(8);
        feed_content.setElevation(12);
    }

    private void setupListener() {

        feed_content.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onItemClicked(feedId);
            }
        });

        feed_image.setOnClickListener(v -> showImageDialog());
    }

    private void showImageDialog() {
        ImageDialog dialog;
        if (mEntity.getRepImg().size() != 0) {
            dialog = mDialogFactory.newImageDialog(mEntity.getRepImg().get(0).getFilepath());
        } else {
            dialog = mDialogFactory.newImageDialog(null);
        }
        mDialogManager.showRetainedDialogWithId(dialog, SHOW_FEED_IMAGE_DIALOG);
    }

    @Override
    public void bindFeed(FeedEntity entity) {

        mEntity = entity;
        feedId = entity.getRepId();

        if (entity.getRepImg() != null && entity.getRepImg().size() != 0) {
            mImageLoader.load(
                    entity.getRepImg().get(0).getFilepath(),
                    R.drawable.img_placeholder_magazine, feed_image);
        } else {
            mImageLoader.load(R.drawable.img_placeholder_magazine, feed_image);
        }

        feed_address.setText(entity.getRepAddr());
        feed_detail_address.setText(entity.getRepDetailAddr());
        feed_date.setText(DateUtil.fromLongToDate(entity.getRepDate()));

        switch (entity.getRepState()) {
            case RECEIVING:
            case ACCEPTED:
                feed_state.setImageResource(R.drawable.ic_map_marker_red);
                break;
            case FOUND:
                feed_state.setImageResource(R.drawable.ic_map_marker_white);
                break;
            case CLEAN:
                feed_state.setImageResource(R.drawable.ic_map_marker_green);
                break;
        }
    }

}
