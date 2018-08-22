package com.limefriends.molde.ui.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.ui.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.ui.mypage.comment.MyCommentActivity;
import com.limefriends.molde.ui.mypage.inquiry.InquiryActivity;
import com.limefriends.molde.ui.mypage.login.LoginActivity;
import com.limefriends.molde.ui.mypage.report.MyFeedActivity;
import com.limefriends.molde.ui.mypage.scrap.ScrapActivity;
import com.limefriends.molde.ui.mypage.settings.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyPageFragment extends Fragment implements MoldeMainActivity.OnKeyBackPressedListener {
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

    //구글 로그인 완료
    private static final int CONNECT_GOOGLE_AUTH_CODE = 1002;
    //페북 로그인 완료
    private static final int CONNECT_FACEBOOK_AUTH_CODE = 1003;

    //파이어베이스 인증 클라이언트
    private static final int RC_SIGN_IN = 9001;

    public static MyPageFragment newInstance() {
        return new MyPageFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("호출확인", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("호출확인", "onStop");
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("호출확인", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("호출확인", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("호출확인", "onDetach");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("호출확인", "onCreateView");

        View view = inflater.inflate(R.layout.mypage_fragment, container, false);
        ButterKnife.bind(this, view);

        // TODO 로그인하면서 체크할 것
        // final String userId = getUserId();

        mypage_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView().findViewById(R.id.mypage_layout), "프로필 이미지", Snackbar.LENGTH_SHORT).show();
            }
        });

        // 설정페이지로 이동
        mypage_setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                intent.putExtra("title", "설정");
                startActivity(intent);
            }
        });

        //문의하기 버튼
        mypage_faq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (getUserId().equals("")) {
////                    startActivity(new Intent(getContext(), LoginActivity.class));
//                    Snackbar.make(mypage_layout, "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
//                } else {
                startActivity(new Intent(getContext(), InquiryActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

//                }
            }
        });

        // 내 신고
        mypage_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getUserId().equals("")) {
//                    startActivity(new Intent(getContext(), LoginActivity.class));
                    Snackbar.make(mypage_layout, "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
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
//                    startActivity(new Intent(getContext(), LoginActivity.class));
                    Snackbar.make(mypage_layout, "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
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
//                    startActivity(new Intent(getContext(), LoginActivity.class));
                    Snackbar.make(mypage_layout, "몰디 로그인이 필요합니다!", Snackbar.LENGTH_SHORT).show();
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
                if (mypage_log_in_out_button.getText().equals("로그아웃")) {

                    MoldeApplication app = ((MoldeApplication) getActivity().getApplication());

                    GoogleSignInClient ggClient = app.getGoogleClient();
                    LoginManager fireBaseLoginManager = app.getFireBaseLoginManager();

                    if (ggClient != null) {
                        ggClient.signOut();
                    } else if (fireBaseLoginManager != null) {
                        fireBaseLoginManager.logOut();
                    }
                    ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth().signOut();
                    ((MoldeApplication) getActivity().getApplication()).setFireBaseAuth(null);
                    // MoldeApplication.firebaseAuth.signOut();
                    mypage_log_in_out_button.setText("로그인");
                    Snackbar.make(getView().findViewById(R.id.mypage_layout), "계정 로그아웃 되었습니다.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivityForResult(intent, RC_SIGN_IN);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            switch (resultCode) {
                case CONNECT_GOOGLE_AUTH_CODE:
                    Snackbar.make(getView().findViewById(R.id.mypage_layout), "구글 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show();
                    break;
                case CONNECT_FACEBOOK_AUTH_CODE:
                    Snackbar.make(getView().findViewById(R.id.mypage_layout), "페이스북 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MoldeMainActivity) context).setOnKeyBackPressedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("호출확인", "onResume");

        FirebaseAuth auth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();

        if (auth != null && auth.getUid() != null) {
            mypage_log_in_out_button.setText("로그아웃");
        } else {
            mypage_log_in_out_button.setText("로그인");
        }
    }

    @Override
    public void onBackKey() {
    }

    private FirebaseAuth mAuth;

    private String getUserId() {
        // return PreferenceUtil.getString(getContext(), "userId");

        FirebaseAuth mAuth = ((MoldeApplication) getActivity().getApplication()).getFireBaseAuth();

        if (mAuth != null && mAuth.getUid() != null) {
            return "lkj";
        } else {
            return "";
        }
    }

}
