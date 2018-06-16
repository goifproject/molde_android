package com.limefriends.molde.molde_tutorial;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.limefriends.molde.R;
import com.limefriends.molde.menu_mypage.MoldeMypageLoginActivity;
import com.limefriends.molde.molde_sub_desc.MoldeSubDescActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeTutorialActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_molde_tutorial);
        ButterKnife.bind(this);
        molde_tutorial_pager.setAdapter(new MoldeTutorialAdapter(getSupportFragmentManager()));
        molde_tutorial_end_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MoldeMypageLoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });
    }

    private class MoldeTutorialAdapter extends FragmentPagerAdapter {
        public MoldeTutorialAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(0 > position || TUTORIAL_PAGE_CNT <= position) return null;
            switch (position){
                case 0 :
                    return MoldeTutorialFirstFragment.newInstance();
                case 1 :
                    return MoldeTutorialSecondFragment.newInstance();
                case 2 :
                    return MoldeTutorialThirdFragment.newInstance();
                case 3 :
                    return MoldeTutorialForthFragment.newInstance();
                default :
                    return null;
            }
        }

        @Override
        public int getCount() {
            return TUTORIAL_PAGE_CNT;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN_REQUEST_CODE){
            switch (resultCode){
                case SKIP_LOGIN_CODE :
                    Intent intent1 =new Intent(getApplicationContext(), MoldeSubDescActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent1);
                    finish();
                    break;
                case CONNECT_GOOGLE_AUTH_CODE :
                    Intent intent2 =new Intent(getApplicationContext(), MoldeSubDescActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent2);
                    finish();
                    break;
                case CONNECT_FACEBOOK_AUTH_CODE :
                    Intent intent3 =new Intent(getApplicationContext(), MoldeSubDescActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent3);
                    finish();
                    break;
                default :
                    //Snackbar.make(findViewById(R.id.activity_molde_tutorial), "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
