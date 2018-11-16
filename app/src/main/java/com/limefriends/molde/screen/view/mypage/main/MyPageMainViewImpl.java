package com.limefriends.molde.screen.view.mypage.main;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limefriends.molde.R;
import com.limefriends.molde.screen.common.dialog.DialogFactory;
import com.limefriends.molde.screen.common.dialog.DialogManager;
import com.limefriends.molde.screen.common.dialog.view.PromptDialog;
import com.limefriends.molde.screen.common.imageLoader.ImageLoader;
import com.limefriends.molde.screen.common.toastHelper.ToastHelper;
import com.limefriends.molde.screen.common.view.BaseObservableView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.limefriends.molde.screen.controller.mypage.main.MyPageFragment.SIGN_OUT_DIALOG;

public class MyPageMainViewImpl
        extends BaseObservableView<MyPageMainView.Listener> implements MyPageMainView {

    private CircleImageView mypage_profile_image;
    private TextView mypage_profile_name;
    private ImageView mypage_setting_button;
    private ImageView mypage_report_button;
    private ImageView mypage_comment_button;
    private ImageView mypage_scrap_button;
    private TextView mypage_faq_button;
    private TextView mypage_log_in_out_button;
    private LinearLayout mypage_layout;

    private DialogManager mDialogManager;
    private DialogFactory mDialogFactory;
    private ImageLoader mImageLoader;
    private ToastHelper mToastHelper;
    private boolean hasSignedIn = false;

    public MyPageMainViewImpl(LayoutInflater inflater,
                              ViewGroup parent,
                              DialogFactory dialogFactory,
                              DialogManager dialogManager,
                              ImageLoader imageLoader,
                              ToastHelper toastHelper) {

        this.mDialogFactory = dialogFactory;
        this.mDialogManager = dialogManager;
        this.mImageLoader = imageLoader;
        this.mToastHelper = toastHelper;

        setRootView(inflater.inflate(R.layout.fragment_mypage, parent, false));

        setupViews();

        setupListeners();
    }

    private void setupViews() {

        mypage_profile_image = findViewById(R.id.mypage_profile_image);
        mypage_profile_name = findViewById(R.id.mypage_profile_name);
        mypage_setting_button = findViewById(R.id.mypage_setting_button);
        mypage_report_button = findViewById(R.id.mypage_report_button);
        mypage_comment_button = findViewById(R.id.mypage_comment_button);
        mypage_scrap_button = findViewById(R.id.mypage_scrap_button);
        mypage_faq_button = findViewById(R.id.mypage_faq_button);
        mypage_log_in_out_button = findViewById(R.id.mypage_log_in_out_button);
        mypage_layout = findViewById(R.id.mypage_layout);
    }

    private void setupListeners() {

        // 설정페이지로 이동
        mypage_setting_button.setOnClickListener(v -> {
            for (Listener listener : getListeners()) {
                listener.onSettingsClicked();
            }
        });

        // 문의하기 버튼
        mypage_faq_button.setOnClickListener(view -> {
            if (!hasSignedIn) {
                mToastHelper.showSnackBar(mypage_layout,
                        getContext().getText(R.string.snackbar_require_signin).toString());
            } else {
                for (Listener listener : getListeners()) {
                    listener.onFaqClicked();
                }
            }
        });

        // 내 신고
        mypage_report_button.setOnClickListener(v -> {
            if (!hasSignedIn) {
                mToastHelper.showSnackBar(mypage_layout,
                        getContext().getText(R.string.snackbar_require_signin).toString());
            } else {
                for (Listener listener : getListeners()) {
                    listener.onMyReportClicked();
                }
            }
        });

        // 내가 쓴 댓글
        mypage_comment_button.setOnClickListener(v -> {
            if (!hasSignedIn) {
                mToastHelper.showSnackBar(mypage_layout,
                        getContext().getText(R.string.snackbar_require_signin).toString());
            } else {
                for (Listener listener : getListeners()) {
                    listener.onMyCommentClicked();
                }
            }
        });

        // 내 스크랩
        mypage_scrap_button.setOnClickListener(v -> {
            if (!hasSignedIn) {
                mToastHelper.showSnackBar(mypage_layout,
                        getContext().getText(R.string.snackbar_require_signin).toString());
            } else {
                for (Listener listener : getListeners()) {
                    listener.onScrapClicked();
                }
            }
        });

        // 로그인 페이지 OR 로그아웃
        mypage_log_in_out_button.setOnClickListener(v -> {
            if (hasSignedIn) {
                showAskLogoutDialog();
            } else {
                for (Listener listener : getListeners()) {
                    listener.onLoginClicked();
                }
            }
        });
    }

    private void showAskLogoutDialog() {
        PromptDialog promptDialog = mDialogFactory.newPromptDialog(
                getContext().getText(R.string.dialog_msg_signout).toString(),
                "",
                getContext().getText(R.string.yes).toString(),
                getContext().getText(R.string.no).toString());
        promptDialog.registerListener(new PromptDialog.PromptDialogDismissListener() {
            @Override
            public void onPositiveButtonClicked() {
                for (Listener listener : getListeners()) {
                    listener.onSignOutClicked();
                }
                mypage_log_in_out_button.setText(getContext().getText(R.string.signin));
                mypage_profile_name.setText("Guest");
                mypage_profile_image.setImageResource(R.drawable.ic_profile);
                mToastHelper.showSnackBar(mypage_layout,
                        getContext().getText(R.string.snackbar_signed_out).toString());
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        mDialogManager.showRetainedDialogWithId(promptDialog, SIGN_OUT_DIALOG);
    }

    @Override
    public void bindLoginStatus(boolean hasSignedIn, String email, String displayName) {
        this.hasSignedIn = hasSignedIn;
        if (hasSignedIn) {
            mypage_log_in_out_button.setText(getContext().getText(R.string.signout));
            if (!displayName.equals("")) {
                mypage_profile_name.setText(displayName);
            } else if (!email.equals("")) {
                mypage_profile_name.setText(email);
            }
        } else {
            mypage_log_in_out_button.setText(getContext().getText(R.string.signin));
        }
    }

    @Override
    public void bindProfilePhoto(Uri uri) {
        mImageLoader.load(uri, mypage_profile_image);
    }

    @Override
    public void showSnackBar(String message) {
        mToastHelper.showSnackBar(mypage_layout, message);
    }

}
