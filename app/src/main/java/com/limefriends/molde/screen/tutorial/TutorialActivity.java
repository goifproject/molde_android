package com.limefriends.molde.screen.tutorial;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.limefriends.molde.R;
import com.limefriends.molde.common.utils.PreferenceUtil;
import com.limefriends.molde.screen.mypage.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import static com.limefriends.molde.screen.mypage.MyPageFragment.SIGNIN_TYPE;

public class TutorialActivity extends AppCompatActivity {

    private final int TUTORIAL_PAGE_CNT = 4;
    //로그인 요청 코드
    public static final int LOGIN_REQUEST_CODE = 1000;
    //로그인 안하고 건너뜀
    private static final int SKIP_LOGIN_CODE = 1001;
    //구글 로그인 완료
    private static final int CONNECT_GOOGLE_AUTH_CODE = 1002;
    //페북 로그인 완료
    private static final int CONNECT_FACEBOOK_AUTH_CODE = 1003;

    @BindView(R.id.molde_tutorial_pager)
    ViewPager molde_tutorial_pager;
    @BindView(R.id.molde_tutorial_end_button)
    Button molde_tutorial_end_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);
        molde_tutorial_pager.setAdapter(new MoldeTutorialAdapter());
        // molde_tutorial_pager.setOffscreenPageLimit(TUTORIAL_PAGE_CNT);
        molde_tutorial_end_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.putBoolean(TutorialActivity.this, "skipFirst", true);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });
        molde_tutorial_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    molde_tutorial_end_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MoldeTutorialAdapter extends PagerAdapter {

        private int extractResId(Context context, String resName) {
            String packName = context.getPackageName(); // 패키지명
            return getResources().getIdentifier(resName, "drawable", packName);
        }

        @Override
        public int getCount() {
            return TUTORIAL_PAGE_CNT;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_tutorial, container, false);
            ImageView imageView = view.findViewById(R.id.tutorial_image);
            String resName = "img_tutorial_" + (position + 1);
            imageView.setImageResource(extractResId(container.getContext(), resName));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST_CODE) {
            switch (resultCode) {
                case SKIP_LOGIN_CODE:
                    Intent intent1 = new Intent(getApplicationContext(), SubTutorialActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent1.putExtra("authLoginDone", SKIP_LOGIN_CODE);
                    startActivity(intent1);
                    finish();
                    break;
                case CONNECT_GOOGLE_AUTH_CODE:
                    Intent intent2 = new Intent(getApplicationContext(), SubTutorialActivity.class);
                    PreferenceUtil.putInt(this, SIGNIN_TYPE, CONNECT_GOOGLE_AUTH_CODE);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent2.putExtra("authLoginDone", CONNECT_GOOGLE_AUTH_CODE);
                    startActivity(intent2);
                    finish();
                    break;
                case CONNECT_FACEBOOK_AUTH_CODE:
                    PreferenceUtil.putInt(this, SIGNIN_TYPE, CONNECT_FACEBOOK_AUTH_CODE);
                    Intent intent3 = new Intent(getApplicationContext(), SubTutorialActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent3.putExtra("authLoginDone", CONNECT_FACEBOOK_AUTH_CODE);
                    startActivity(intent3);
                    finish();
                    break;
                default:
                    // Snackbar.make(findViewById(R.id.activity_tutorial), "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
