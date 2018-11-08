package com.limefriends.molde.screen.mypage.faq;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseActivity;
import com.limefriends.molde.screen.mypage.faq.view.FaqView;

import butterknife.BindView;
import butterknife.ButterKnife;

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
