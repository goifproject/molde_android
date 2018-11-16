package com.limefriends.molde.screen.view.mypage.faq;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.toolbar.NestedToolbar;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import com.limefriends.molde.screen.common.view.ViewFactory;

public class FaqViewImpl
        extends BaseObservableView<FaqView.Listener> implements FaqView, View.OnClickListener {

    private TextView
            faq1, faq2, faq3, faq4, faq5;
    private TextView
            answer1, answer2, answer3, answer4, answer5;
    private Toolbar mToolbar;
    private NestedToolbar mNestedToolbar;

    private ViewFactory mViewFactory;

    public FaqViewImpl(LayoutInflater inflater,
                       ViewGroup parent,
                       ViewFactory viewFactory) {

        setRootView(inflater.inflate(R.layout.activity_faq, parent, false));

        this.mViewFactory = viewFactory;

        setupViews();

        setupToolbar();

        setupListener();
    }

    private void setupViews() {
        faq1 = findViewById(R.id.faq1);
        faq2 = findViewById(R.id.faq2);
        faq3 = findViewById(R.id.faq3);
        faq4 = findViewById(R.id.faq4);
        faq5 = findViewById(R.id.faq5);

        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        answer5 = findViewById(R.id.answer5);

        mToolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mNestedToolbar = mViewFactory.newInstance(NestedToolbar.class, mToolbar);
        mToolbar.addView(mNestedToolbar.getRootView());
        mNestedToolbar.setTitle(getContext().getText(R.string.faq).toString());
    }

    private void setupListener() {

        mNestedToolbar.enableUpButtonAndListen(() -> {
            for (Listener listener : getListeners()) {
                listener.onNavigateUpClicked();
            }
        });

        faq1.setOnClickListener(this);
        faq2.setOnClickListener(this);
        faq3.setOnClickListener(this);
        faq4.setOnClickListener(this);
        faq5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.faq1:
                answer1.setVisibility(View.VISIBLE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq2:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.VISIBLE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq3:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.VISIBLE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq4:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.VISIBLE);
                answer5.setVisibility(View.GONE);
                break;

            case R.id.faq5:
                answer1.setVisibility(View.GONE);
                answer2.setVisibility(View.GONE);
                answer3.setVisibility(View.GONE);
                answer4.setVisibility(View.GONE);
                answer5.setVisibility(View.VISIBLE);
                break;
        }
    }

}
