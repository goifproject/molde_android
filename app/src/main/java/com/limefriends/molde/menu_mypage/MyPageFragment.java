package com.limefriends.molde.menu_mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.limefriends.molde.MoldeApplication;
import com.limefriends.molde.MoldeMainActivity;
import com.limefriends.molde.R;
import com.limefriends.molde.menu_mypage.comment.MoldeMyPageMyCommentActivity;
import com.limefriends.molde.menu_mypage.report.MypageMyReportActivity;
import com.limefriends.molde.menu_mypage.scrap.MypageMyScrapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.MoldeApplication.fbLoginManager;
import static com.limefriends.molde.MoldeApplication.firebaseAuth;
import static com.limefriends.molde.MoldeApplication.ggClient;

public class MyPageFragment extends Fragment implements MoldeMainActivity.onKeyBackPressedListener {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mypage_fragment, container, false);
        ButterKnife.bind(this, view);

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
                Intent intent = new Intent(getContext(), MyPageSettingsActivity.class);
                intent.putExtra("title", "설정");
                startActivity(intent);
            }
        });

        //문의하기 버튼
        mypage_faq_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyPageInquiryActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));
            }
        });

        // 내 신고
        mypage_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MypageMyReportActivity.class);
                startActivity(intent);
            }
        });

        // 내가 쓴 댓글
        mypage_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoldeMyPageMyCommentActivity.class);
                startActivity(intent);
            }
        });

        //내 스크랩
        mypage_scrap_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MypageMyScrapActivity.class);
                startActivity(intent);
            }
        });

        //로그인 페이지 OR 로그아웃
        mypage_log_in_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mypage_log_in_out_button.getText().equals("로그아웃")) {
                    if (ggClient != null) {
                        ggClient.signOut();
                    } else if (fbLoginManager != null) {
                        fbLoginManager.logOut();
                    }
                    MoldeApplication.firebaseAuth.signOut();
                    mypage_log_in_out_button.setText("로그인");
                    Snackbar.make(getView().findViewById(R.id.mypage_layout), "계정 로그아웃 되었습니다.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getContext(), MyPageLoginActivity.class);
                    startActivityForResult(intent, RC_SIGN_IN);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            switch (resultCode) {
                case CONNECT_GOOGLE_AUTH_CODE :
                    Snackbar.make(getView().findViewById(R.id.mypage_layout), "구글 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show();
                    break;
                case CONNECT_FACEBOOK_AUTH_CODE :
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
        if (firebaseAuth.getUid() != null) {
            mypage_log_in_out_button.setText("로그아웃");
        } else {
            mypage_log_in_out_button.setText("로그인");
        }
    }

    @Override
    public void onBackKey() {
    }

}
