package com.limefriends.molde.screen.common.dialog.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.screen.common.viewController.BaseDialog;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;

public class ImageDialog extends BaseDialog {

    /* package */ public static final String ARG_IMAGE_URL = "ARG_IMAGE_URL";

    private ImageView feed_dialog_thumbnail_image;
    private ImageView feed_dialog_close_button;

    @Service ImageLoader mImageLoader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getInjector().inject(this);
    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_feed_image, container, false);
//        ButterKnife.bind(this, view);
//        if (imageUrl != null) {
//            Glide.with(this)
//                    .load(imageUrl)
//                    .placeholder(R.drawable.img_placeholder_magazine)
//                    .into(new SimpleTarget<GlideDrawable>() {
//                        @Override
//                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
//                            WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
//                            params.width = getResources().getDisplayMetrics().widthPixels;
//                            params.height = getResources().getDisplayMetrics().heightPixels;
//                            getDialog().getWindow().setAttributes(params);
//                            feed_dialog_thumbnail_image.setImageDrawable(resource);
//                        }
//                    });
//        } else {
//            Glide.with(this)
//                    .load(R.drawable.img_placeholder_magazine)
//                    .into(feed_dialog_thumbnail_image);
//        }
//        feed_dialog_close_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//        return view;
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_feed_image, null);
        dialogBuilder.setView(dialogView);

        initSubViews(dialogView);

        AlertDialog dialog = dialogBuilder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
                params.width = getResources().getDisplayMetrics().widthPixels;
                params.height = getResources().getDisplayMetrics().heightPixels;
                getDialog().getWindow().setAttributes(params);

                FrameLayout.LayoutParams imageParam = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

                feed_dialog_thumbnail_image.setLayoutParams(imageParam);

                String imageUrl = getArguments().getString(ARG_IMAGE_URL);
                mImageLoader.load(imageUrl, feed_dialog_thumbnail_image);
            }
        });

        populateSubViews();

        setCancelable(true);

        return dialog;
    }


    private void initSubViews(View rootView) {
        feed_dialog_close_button = rootView.findViewById(R.id.feed_dialog_close_button);
        feed_dialog_thumbnail_image = rootView.findViewById(R.id.feed_dialog_thumbnail_image);

        feed_dialog_close_button.setOnClickListener(v -> dismiss());
    }

    private void populateSubViews() {

        String imageUrl = getArguments().getString(ARG_IMAGE_URL);

        if (imageUrl != null) {
            // mImageLoader.load(imageUrl, feed_dialog_thumbnail_image);

            mImageLoader.load(imageUrl, new SimpleTarget<GlideDrawable>() {
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
            mImageLoader.load(R.drawable.img_placeholder_magazine, feed_dialog_thumbnail_image);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}
