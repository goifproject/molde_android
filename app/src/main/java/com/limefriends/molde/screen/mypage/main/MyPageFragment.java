package com.limefriends.molde.screen.mypage.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.limefriends.molde.common.app.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.common.di.Service;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.screen.common.screensNavigator.ActivityScreenNavigator;
import com.limefriends.molde.screen.common.view.ViewFactory;
import com.limefriends.molde.screen.common.viewController.BaseFragment;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.mypage.comment.MyCommentActivity;
import com.limefriends.molde.screen.mypage.inquiry.InquiryActivity;
import com.limefriends.molde.screen.mypage.login.LoginActivity;
import com.limefriends.molde.screen.mypage.main.view.MyPageMainView;
import com.limefriends.molde.screen.mypage.report.MyFeedActivity;
import com.limefriends.molde.screen.mypage.scrap.ScrapActivity;
import com.limefriends.molde.screen.mypage.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.limefriends.molde.common.Constant.MyPage.*;

public class MyPageFragment extends BaseFragment implements MyPageMainView.Listener {

    public static MyPageFragment newInstance() {
        MyPageFragment myPageFragment = new MyPageFragment();
        return myPageFragment;
    }

    public static final String SIGNIN_TYPE = "signinType";
    public static final String SIGN_OUT_DIALOG = "SIGN_OUT_DIALOG";

    private FirebaseAuth mAuth;

    @Service private ViewFactory mViewFactory;
    @Service private ActivityScreenNavigator mActivityScreenNavigator;
    private MyPageMainView mMyPageMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getInjector().inject(this);

        mMyPageMainView = mViewFactory.newInstance(MyPageMainView.class, null);

        return mMyPageMainView.getRootView();
    }

    @Override
    public void onStart() {
        super.onStart();

        mMyPageMainView.registerListener(this);

        mAuth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();

        bindLoginStatus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            switch (resultCode) {
                case CONNECT_GOOGLE_AUTH_CODE:
                    PreferenceUtil.putInt(getContext(), SIGNIN_TYPE, CONNECT_GOOGLE_AUTH_CODE);
                    mMyPageMainView.showSnackBar(getText(R.string.signin_google).toString());
                    break;
                case CONNECT_FACEBOOK_AUTH_CODE:
                    PreferenceUtil.putInt(getContext(), SIGNIN_TYPE, CONNECT_FACEBOOK_AUTH_CODE);
                    mMyPageMainView.showSnackBar(getText(R.string.signin_facebook).toString());
                    break;
            }
            bindLoginStatus();
        }
    }

    private void bindLoginStatus() {
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            mMyPageMainView.bindLoginStatus(true,
                    mAuth.getCurrentUser().getEmail(),
                    mAuth.getCurrentUser().getDisplayName());
            mMyPageMainView.bindProfilePhoto(mAuth.getCurrentUser().getPhotoUrl());
        } else {
            mMyPageMainView.bindLoginStatus(false, "", "");
        }
    }

    @Override
    public void onSettingsClicked() {
        mActivityScreenNavigator.toSettingsActivity();
    }

    @Override
    public void onFaqClicked() {
        mActivityScreenNavigator.toInquiryActivity();
    }

    @Override
    public void onMyReportClicked() {
        mActivityScreenNavigator.toMyFeedActivity();
    }

    @Override
    public void onMyCommentClicked() {
        mActivityScreenNavigator.toMyCommentActivity();
    }

    @Override
    public void onScrapClicked() {
        mActivityScreenNavigator.toScrapActivity();
    }

    @Override
    public void onLoginClicked() {
        mActivityScreenNavigator.toLoginActivity(getActivity());
    }

    @Override
    public void onSignOutClicked() {
        int type = PreferenceUtil.getInt(getContext(), SIGNIN_TYPE, 0);
        if (type == CONNECT_GOOGLE_AUTH_CODE) {
            ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth().signOut();
        } else if (type == CONNECT_FACEBOOK_AUTH_CODE) {
            ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth().signOut();
            LoginManager.getInstance().logOut();
        }
        bindLoginStatus();
    }

}
