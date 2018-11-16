package com.limefriends.molde.screen.view.mypage.login;

import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.view.BaseObservableView;

public class LoginViewImpl
        extends BaseObservableView<LoginView.Listener> implements LoginView {

    private RelativeLayout login_google_button;
    private RelativeLayout login_facebook_button;
    private TextView login_to_google;
    private TextView skip_login_button;
    private TextView login_to_facebook;
    private ProgressDialog loginProgressDialog;

    public LoginViewImpl(LayoutInflater inflater,
                         ViewGroup parent) {
        setRootView(inflater.inflate(R.layout.activity_login, parent, false));

        setupViews();

        setupListener();
    }

    private void setupViews() {
        login_google_button = findViewById(R.id.login_google_button);
        login_facebook_button = findViewById(R.id.login_facebook_button);
        login_to_google = findViewById(R.id.login_to_google);
        skip_login_button = findViewById(R.id.skip_login_button);
        loginProgressDialog = new ProgressDialog(getContext());
        loginProgressDialog.setTitle("로그인 중입니다...");
    }

    private void setupListener() {

        // 구글 로그인
        login_google_button.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onGoogleLoginClicked();
            }
        });

        // 로그인 스킵
        skip_login_button.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onSkipLoginClicked();
            }
        });
    }


    @Override
    public void showProgressIndication() {
        loginProgressDialog.show();
    }

    @Override
    public void hideProgressIndication() {
        loginProgressDialog.dismiss();
    }
}
