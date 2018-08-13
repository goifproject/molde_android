package com.limefriends.molde.comm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

public class MoldeApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context moldeContext;
    public static FirebaseAuth firebaseAuth;
    public static GoogleSignInClient ggClient;
    public static LoginManager fbLoginManager;
    public static final String BASE_URL = "http://13.209.64.183:7019";
    public static LatLng myLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        moldeContext = this;
        firebaseAuth = FirebaseAuth.getInstance();
    }
    public static Context getMoldeContext(){
        return moldeContext;
    }
}
