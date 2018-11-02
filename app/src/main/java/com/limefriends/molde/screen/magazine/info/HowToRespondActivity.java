package com.limefriends.molde.screen.magazine.info;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.model.entity.molcaInfo.MolcaInfo;
import com.limefriends.molde.screen.common.controller.BaseActivity;
import com.limefriends.molde.screen.common.views.ViewFactory;
import com.limefriends.molde.screen.magazine.info.view.MolcaInfoView;

import java.util.ArrayList;
import java.util.List;

public class HowToRespondActivity extends BaseActivity implements MolcaInfoView.Listener {

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, HowToRespondActivity.class);
        intent.putExtra("title", "몰카유포 대처");
        context.startActivity(intent);
    }

    @Service
    ViewFactory mViewFactory;

    private MolcaInfoView mMolcaInfoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        mMolcaInfoView = mViewFactory.newInstance(MolcaInfoView.class, null);

        setContentView(mMolcaInfoView.getRootView());
    }

    @Override
    protected void onStart() {
        super.onStart();
        String title = getIntent().getStringExtra("title");
        mMolcaInfoView.bindTitle(title);
        mMolcaInfoView.registerListener(this);
        bindInfo();
    }

    private void bindInfo() {
        List<MolcaInfo> infoList = new ArrayList<>();
        String[] content = getResources().getStringArray(R.array.info_detect_content);
        String[] title = getResources().getStringArray(R.array.info_detect_title);

        for (int i = 0; i < content.length; i++) {

            int resId = getResources().getIdentifier(
                    "img_respond_info_0"+(i+1), "drawable", getPackageName());

            infoList.add(new MolcaInfo(title[i], content[i], resId));
        }
        mMolcaInfoView.bindInfo(infoList);
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
