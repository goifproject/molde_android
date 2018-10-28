package com.limefriends.molde.screen.magazine.info;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.magazine.info.view.HowToDetectView;

public class HowToDetectActivity extends BaseActivity implements HowToDetectView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, HowToDetectActivity.class);
        intent.putExtra("title", "장소별 대처법");
        context.startActivity(intent);
    }

    @Service ViewFactory mViewFactory;

    private HowToDetectView mHowToDetectView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mHowToDetectView = mViewFactory.newInstance(HowToDetectView.class, null);

        setContentView(mHowToDetectView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        String title = getIntent().getStringExtra("title");
        mHowToDetectView.bindTitle(title);

        mHowToDetectView.registerListener(this);
    }

    @Override
    public void onReportClicked() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
        startActivity(intent);
    }

    @Override
    public void onNavigateUpClicked() {
        onBackPressed();
    }
}
