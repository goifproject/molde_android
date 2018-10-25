package com.limefriends.molde.screen.mypage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
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
import com.limefriends.molde.common.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.common.utils.PreferenceUtil;
import com.limefriends.molde.screen.mypage.comment.MyCommentActivity;
import com.limefriends.molde.screen.mypage.inquiry.InquiryActivity;
import com.limefriends.molde.screen.mypage.login.LoginActivity;
import com.limefriends.molde.screen.mypage.report.MyFeedActivity;
import com.limefriends.molde.screen.mypage.scrap.ScrapActivity;
import com.limefriends.molde.screen.mypage.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.limefriends.molde.common.Constant.MyPage.*;

public class MyPageFragment extends Fragment {

    public static final String SIGNIN_TYPE = "signinType";

    @BindView(R.id.mypage_profile_image)
    CircleImageView mypage_profile_image;
    @BindView(R.id.mypage_profile_name)
    TextView mypage_profile_name;
    @BindView(R.id.mypage_setting_button)
    ImageView mypage_setting_button;
    @BindView(R.id.mypage_report_button)
    ImageView mypage_report_button;
    @BindView(R.id.mypage_comment_button)
    ImageView mypage_comment_button;
    @BindView(R.id.mypage_scrap_button)
    ImageView mypage_scrap_button;
    @BindView(R.id.mypage_faq_button)
    TextView mypage_faq_button;
    @BindView(R.id.mypage_log_in_out_button)
    TextView mypage_log_in_out_button;
    @BindView(R.id.mypage_layout)
    LinearLayout mypage_layout;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        mAuth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();

        setupViews(view);

        setupListeners();

        return view;
    }

    //-----
    // View
    //-----

    private void setupViews(View view) {
        ButterKnife.bind(this, view);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mypage_log_in_out_button.setText(getText(R.string.signout));
            if (user.getDisplayName() != null) {
                mypage_profile_name.setText(user.getDisplayName());
            }
            else if (user.getEmail() != null) {
                mypage_profile_name.setText(user.getEmail());
            }
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(mypage_profile_image);
            }
        } else {
            mypage_log_in_out_button.setText(getText(R.string.signin));
        }
    }

    private void setupListeners() {

        // 설정페이지로 이동
        mypage_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        // 문의하기 버튼
        mypage_faq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getUserId().equals("")) {
                    snackBar(getText(R.string.snackbar_require_signin).toString());
                } else {
                    Intent intent = new Intent(getContext(), InquiryActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 내 신고
        mypage_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getUserId().equals("")) {
                    snackBar(getText(R.string.snackbar_require_signin).toString());
                } else {
                    Intent intent = new Intent(getContext(), MyFeedActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 내가 쓴 댓글
        mypage_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getUserId().equals("")) {
                    snackBar(getText(R.string.snackbar_require_signin).toString());
                } else {
                    Intent intent = new Intent(getContext(), MyCommentActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 내 스크랩
        mypage_scrap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getUserId().equals("")) {
                    snackBar(getText(R.string.snackbar_require_signin).toString());
                } else {
                    Intent intent = new Intent(getContext(), ScrapActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 로그인 페이지 OR 로그아웃
        mypage_log_in_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mypage_log_in_out_button.getText().equals(getText(R.string.signout))) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                            .setMessage(getText(R.string.dialog_msg_signout))
                            .setPositiveButton(Html.fromHtml("<font color='#000000'>예</font>"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int type = PreferenceUtil.getInt(getContext(), SIGNIN_TYPE, 0);
                                    if (type == CONNECT_GOOGLE_AUTH_CODE) {
                                        Log.e("호출확인", "CONNECT_GOOGLE_AUTH_CODE");
                                        ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth().signOut();
                                    }
                                    else if (type == CONNECT_FACEBOOK_AUTH_CODE) {
                                        Log.e("호출확인", "CONNECT_FACEBOOK_AUTH_CODE");
                                        ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth().signOut();
                                        LoginManager.getInstance().logOut();
                                    }
                                    mypage_log_in_out_button.setText(getText(R.string.signin));
                                    mypage_profile_name.setText("Guest");
                                    mypage_profile_image.setImageResource(R.drawable.ic_profile);
                                    snackBar(getText(R.string.snackbar_signed_out).toString());
                                }
                            })
                            .setNegativeButton(Html.fromHtml("<font color='#000000'>아니오</font>"), null)
                            .create();
                    dialog.show();
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(intent, RC_SIGN_IN);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            switch (resultCode) {
                case CONNECT_GOOGLE_AUTH_CODE:
                    mypage_log_in_out_button.setText(getText(R.string.signout));
                    PreferenceUtil.putInt(getContext(), SIGNIN_TYPE, CONNECT_GOOGLE_AUTH_CODE);
                    snackBar(getText(R.string.signin_google).toString());
                    break;
                case CONNECT_FACEBOOK_AUTH_CODE:
                    mypage_log_in_out_button.setText(getText(R.string.signout));
                    PreferenceUtil.putInt(getContext(), SIGNIN_TYPE, CONNECT_FACEBOOK_AUTH_CODE);
                    snackBar(getText(R.string.signin_facebook).toString());
                    break;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                if (user.getDisplayName() != null) {
                    mypage_profile_name.setText(user.getDisplayName());
                }
                else if (user.getEmail() != null) {
                    mypage_profile_name.setText(user.getEmail());
                }
                if (user.getPhotoUrl() != null) {
                    Glide.with(this)
                            .load(user.getPhotoUrl())
                            .into(mypage_profile_image);
                }
            }
        }
    }

    private String getUserId() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        } else {
            return "";
        }
    }

    private void snackBar(String message) {
        Snackbar.make(mypage_layout, message, Snackbar.LENGTH_SHORT).show();
    }

}
