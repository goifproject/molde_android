package com.limefriends.molde.screen.feed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedImageDialog extends AppCompatDialogFragment {

    @BindView(R.id.feed_dialog_thumbnail_image)
    ImageView feed_dialog_thumbnail_image;
    @BindView(R.id.feed_dialog_close_button)
    ImageView feed_dialog_close_button;

    private String imageUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_feed_image, container, false);
        ButterKnife.bind(this, view);
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.img_placeholder_magazine)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
                            params.width = getResources().getDisplayMetrics().widthPixels;
                            params.height = getResources().getDisplayMetrics().heightPixels;
                            getDialog().getWindow().setAttributes(params);
                            feed_dialog_thumbnail_image.setImageDrawable(resource);
                        }
                    });
        } else {
            Glide.with(this)
                    .load(R.drawable.img_placeholder_magazine)
                    .into(feed_dialog_thumbnail_image);
        }
        feed_dialog_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    public void setData(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
