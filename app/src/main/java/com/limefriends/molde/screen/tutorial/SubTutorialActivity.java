package com.limefriends.molde.screen.tutorial;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.screen.main.MoldeMainActivity;
import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubTutorialActivity extends AppCompatActivity {
    private final int SUB_DESC_PAGE_CNT = 4;

    //로그인 안하고 건너뜀
    private static final int SKIP_LOGIN_CODE = 1001;
    //구글 로그인 완료
    private static final int CONNECT_GOOGLE_AUTH_CODE = 1002;
    //페북 로그인 완료
    private static final int CONNECT_FACEBOOK_AUTH_CODE = 1003;

    @BindView(R.id.molde_sub_desc_pager)
    ViewPager molde_sub_desc_pager;
    @BindView(R.id.go_to_start_layout)
    FrameLayout go_to_start_layout;
    @BindView(R.id.go_to_start_button)
    Button go_to_start_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_tutorial);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        switch (intent.getIntExtra("authLoginDone", SKIP_LOGIN_CODE)) {
            case CONNECT_GOOGLE_AUTH_CODE:
                Snackbar.make(findViewById(R.id.sub_desc_layout), "구글 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show();
                break;
            case CONNECT_FACEBOOK_AUTH_CODE:
                Snackbar.make(findViewById(R.id.sub_desc_layout), "페이스북 로그인 되었습니다.", Snackbar.LENGTH_SHORT).show();
                break;
            case SKIP_LOGIN_CODE:
                break;
        }
        molde_sub_desc_pager.setAdapter(new MoldeSubDescAdapter());
        go_to_start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtil.putBoolean(SubTutorialActivity.this, "skipSecond", true);
                Intent intent = new Intent(SubTutorialActivity.this, MoldeMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        molde_sub_desc_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) go_to_start_layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class MoldeSubDescAdapter extends PagerAdapter {

        private int extractResId(Context context, String resName) {
            String packName = context.getPackageName(); // 패키지명
            return getResources().getIdentifier(resName, "drawable", packName);
        }

        @Override
        public int getCount() {
            return SUB_DESC_PAGE_CNT;
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
            ImageView image = view.findViewById(R.id.tutorial_image);
            String resName = "img_sub_tutorial_" + (position + 1);
            image.setImageResource(extractResId(container.getContext(), resName));
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }
    }
}
