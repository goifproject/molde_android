package com.limefriends.molde.screen.magazine.info.view.viewImpl;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.views.BaseObservableView;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.magazine.info.view.HowToDetectView;

public class HowToDetectViewImpl
        extends BaseObservableView<HowToDetectView.Listener> implements HowToDetectView {

    private FloatingActionButton btn_report_call;
    private NestedToolbar mNestedToolbar;
    private Toolbar mToolbar;
    private TextView
            txt_molca_by_location_01,
            txt_molca_by_location_02,
            txt_molca_by_location_03,
            txt_molca_by_location_04,
            txt_molca_by_location_05;
    private ImageView
            img_molca_by_location_01,
            img_molca_by_location_02,
            img_molca_by_location_03,
            img_molca_by_location_04,
            img_molca_by_location_05,
            img_molca_by_location_06,
            img_molca_by_location_07,
            img_molca_by_location_08;

    private final ImageLoader mImageLoader;
    private final ViewFactory mViewFactory;


    public HowToDetectViewImpl(LayoutInflater inflater,
                               ViewGroup parent,
                               ImageLoader imageLoader,
                               ViewFactory viewFactory) {
        setRootView(inflater.inflate(R.layout.activity_info_prevent, parent, false));

        this.mImageLoader = imageLoader;
        this.mViewFactory = viewFactory;

        setupViews();
    }

    private void setupViews() {

        btn_report_call = findViewById(R.id.btn_location_report_call);
        mToolbar = findViewById(R.id.toolbar);
        txt_molca_by_location_01 = findViewById(R.id.txt_molca_by_location_01);
        txt_molca_by_location_02 = findViewById(R.id.txt_molca_by_location_02);
        txt_molca_by_location_03 = findViewById(R.id.txt_molca_by_location_03);
        txt_molca_by_location_04 = findViewById(R.id.txt_molca_by_location_04);
        txt_molca_by_location_05 = findViewById(R.id.txt_molca_by_location_05);

        img_molca_by_location_01 = findViewById(R.id.img_molca_by_location_01);
        img_molca_by_location_02 = findViewById(R.id.img_molca_by_location_02);
        img_molca_by_location_03 = findViewById(R.id.img_molca_by_location_03);
        img_molca_by_location_04 = findViewById(R.id.img_molca_by_location_04);
        img_molca_by_location_05 = findViewById(R.id.img_molca_by_location_05);
        img_molca_by_location_06 = findViewById(R.id.img_molca_by_location_06);
        img_molca_by_location_07 = findViewById(R.id.img_molca_by_location_07);
        img_molca_by_location_08 = findViewById(R.id.img_molca_by_location_08);

        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);

        setupToolbar();

        setupListeners();

        bindInfo();

        bindImages();
    }

    private void setupToolbar() {
        mToolbar.addView(mNestedToolbar.getRootView());
    }

    private void setupListeners() {

        btn_report_call.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onReportClicked();
            }
        });

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });
    }

    @Override
    public void bindTitle(String title) {
        mNestedToolbar.setTitle(title);
    }

    private void bindInfo() {
        String[] info = getContext().getResources().getStringArray(R.array.info_prevention);
        txt_molca_by_location_01.setText(info[0]);
        txt_molca_by_location_02.setText(info[1]);
        txt_molca_by_location_03.setText(info[2]);
        txt_molca_by_location_04.setText(info[3]);
        txt_molca_by_location_05.setText(info[4]);
    }

    private void bindImages() {
        mImageLoader.load(R.drawable.img_prevent_info_01, img_molca_by_location_01);
        mImageLoader.load(R.drawable.img_prevent_info_02, img_molca_by_location_02);
        mImageLoader.load(R.drawable.img_prevent_info_03, img_molca_by_location_03);
        mImageLoader.load(R.drawable.img_prevent_info_04, img_molca_by_location_04);
        mImageLoader.load(R.drawable.img_prevent_info_05, img_molca_by_location_05);
        mImageLoader.load(R.drawable.img_prevent_info_06, img_molca_by_location_06);
        mImageLoader.load(R.drawable.img_prevent_info_07, img_molca_by_location_07);
        mImageLoader.load(R.drawable.img_prevent_info_08, img_molca_by_location_08);
    }
}
