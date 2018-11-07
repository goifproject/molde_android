package com.limefriends.molde.screen.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.limefriends.molde.R;
import com.limefriends.molde.common.helper.PreferenceUtil;
import com.limefriends.molde.screen.tutorial.SubTutorialActivity;
import com.limefriends.molde.screen.tutorial.TutorialActivity;

import static com.limefriends.molde.common.Constant.Authority.*;

public class MoldeSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean firstLaunch = PreferenceUtil.getBoolean(MoldeSplashActivity.this, "isFirst");
                boolean skipFirstTutorial = PreferenceUtil.getBoolean(MoldeSplashActivity.this, "skipFirst");
                boolean skipSecondTutorial = PreferenceUtil.getBoolean(MoldeSplashActivity.this, "skipSecond");

                if (firstLaunch) {
                    PreferenceUtil.putLong(MoldeSplashActivity.this, "authority", GUEST);
                    PreferenceUtil.putBoolean(MoldeSplashActivity.this, "isFirst", false);
                }

                if (skipFirstTutorial) {
                    if (skipSecondTutorial) {
                        Intent intent = new Intent(getApplicationContext(), MoldeMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SubTutorialActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);

    }
}
