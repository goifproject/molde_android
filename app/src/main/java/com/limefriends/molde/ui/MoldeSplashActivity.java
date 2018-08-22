package com.limefriends.molde.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.Constant;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.ui.mypage.login.LoginActivity;
import com.limefriends.molde.ui.tutorial.main_tutorial.MoldeTutorialActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.comm.Constant.Authority.*;

public class MoldeSplashActivity extends AppCompatActivity {

    @BindView(R.id.molde_splash)
    ImageView molde_splash_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_molde_splash);
        ButterKnife.bind(this);
        GlideDrawableImageViewTarget gifSplashImg = new GlideDrawableImageViewTarget(molde_splash_img);
        Glide.with(this).load(R.drawable.molde_splash).into(gifSplashImg);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
                boolean firstLaunch = sharedPreferences.getBoolean("isFirst", true);
                if (firstLaunch) {
                    PreferenceUtil.putLong(MoldeSplashActivity.this, "authority", GUEST);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), MoldeTutorialActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                // TODO 임시로 어드민 설정한 것이니 바로 지울 것
                PreferenceUtil.putLong(MoldeSplashActivity.this, "authority", ADMIN);

                Intent intent = new Intent(getApplicationContext(), MoldeMainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}
