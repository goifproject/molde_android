package com.limefriends.molde;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.limefriends.molde.molde_tutorial.MoldeTutorialActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), MoldeTutorialActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), MoldeMainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}
