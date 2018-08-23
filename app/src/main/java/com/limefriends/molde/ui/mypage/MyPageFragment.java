package com.limefriends.molde.ui.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.ui.mypage.comment.MyCommentActivity;
import com.limefriends.molde.ui.mypage.inquiry.InquiryActivity;
import com.limefriends.molde.ui.mypage.login.LoginActivity;
import com.limefriends.molde.ui.mypage.report.MyFeedActivity;
import com.limefriends.molde.ui.mypage.scrap.ScrapActivity;
import com.limefriends.molde.ui.mypage.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.comm.Constant.MyPage.*;

// TODO "lkj" 바꿔야 함
public class MyPageFragment extends Fragment {

    @BindView(R.id.mypage_profile_image)
    CircleImageView mypage_profile_image;
    @BindView(R.id.mypage_profile_name)
    TextView mypage_profile_name;
    @BindView(R.id.mypage_setting_button)
    ImageButton mypage_setting_button;
    @BindView(R.id.mypage_report_button)
    ImageButton mypage_report_button;
    @BindView(R.id.mypage_comment_button)
    ImageButton mypage_comment_button;
    @BindView(R.id.mypage_scrap_button)
    ImageButton mypage_scrap_button;
    @BindView(R.id.mypage_faq_button)
    Button mypage_faq_button;
    @BindView(R.id.mypage_log_in_out_button)
    Button mypage_log_in_out_button;
    @BindView(R.id.mypage_layout)
    RelativeLayout mypage_layout;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mypage_fragment, container, false);

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
        if (mAuth.getCurrentUser() != null) {
            mypage_log_in_out_button.setText(getText(R.string.signout));
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

        //문의하기 버튼
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

        //내 스크랩
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

        //로그인 페이지 OR 로그아웃
        mypage_log_in_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mypage_log_in_out_button.getText().equals(getText(R.string.signout))) {
//                    MoldeApplication app = ((MoldeApplication) getActivity().getApplication());
//                    GoogleSignInClient ggClient = app.getGoogleClient();
//                    LoginManager fireBaseLoginManager = app.getFireBaseLoginManager();
//
//                    if (ggClient != null) {
//                        ggClient.signOut();
//                        Log.e("호출확인", "ggClient != null");
//                    } else if (fireBaseLoginManager != null) {
//                        fireBaseLoginManager.logOut();
//                        Log.e("호출확인", "fireBaseLoginManager != null");
//                    }
                    ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth().signOut();
                    mypage_log_in_out_button.setText(getText(R.string.signin));
                    snackBar(getText(R.string.snackbar_signed_out).toString());
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
                    snackBar(getText(R.string.signin_google).toString());
                    break;
                case CONNECT_FACEBOOK_AUTH_CODE:
                    snackBar(getText(R.string.signin_facebook).toString());
                    break;
            }
            mypage_log_in_out_button.setText(getText(R.string.signout));
        }
    }

    private String getUserId() {
        if (mAuth.getCurrentUser() != null) {
            return "lkj";
        } else {
            return "";
        }
    }

    private void snackBar(String message) {
        Snackbar.make(mypage_layout, message, Snackbar.LENGTH_SHORT).show();
    }

}
