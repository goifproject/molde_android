package com.limefriends.molde.screen.controller.mypage.faq;

import android.os.Bundle;

import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.view.mypage.faq.FaqView;

public class FaQActivity extends BaseActivity {

    @Service private ViewFactory mViewFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getInjector().inject(this);

        FaqView view = mViewFactory.newInstance(FaqView.class, null);

        setContentView(view.getRootView());
    }
}
