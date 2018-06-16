package com.limefriends.molde;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.limefriends.molde.molde_tutorial.MoldeTutorialActivity;

public class MoldeSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean firstLaunch = sharedPreferences.getBoolean("isFirst", true);
        if(firstLaunch){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirst",false);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MoldeTutorialActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        Intent intent = new Intent(this, MoldeMainActivity.class);
        startActivity(intent);
        finish();
    }
}
