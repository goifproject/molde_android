package com.limefriends.molde;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MoldeApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context moldeContext;
    public static FirebaseAuth firebaseAuth;
    public static LoginManager fbLoginManager;

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth = FirebaseAuth.getInstance();
        fbLoginManager = LoginManager.getInstance();
        moldeContext = this;
    }
    public static Context getMoldeContext(){
        return moldeContext;
    }
}
