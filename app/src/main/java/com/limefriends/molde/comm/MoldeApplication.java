package com.limefriends.molde.comm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

public class MoldeApplication extends Application {

    // @SuppressLint("StaticFieldLeak")
    // private static Context moldeContext;
    public static final String BASE_URL = "http://13.209.64.183:7019";
    private static final double DEFAULT_LAT = 37.5662952;
    private static final double DEFAULT_LNG = 126.97794509999994;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient ggClient;
    private LoginManager fbLoginManager;
    private LatLng myLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        // moldeContext = this;
        firebaseAuth = FirebaseAuth.getInstance();
    }
//
//    public static Context getMoldeContext() {
//        return moldeContext;
//    }

    public FirebaseAuth getFireBaseAuth() {
        return firebaseAuth;
    }

    public void setFireBaseAuth(FirebaseAuth auth) {
        this.firebaseAuth = auth;
    }

    public GoogleSignInClient getGoogleClient() {
        return ggClient;
    }

    public LoginManager getFireBaseLoginManager() {
        return fbLoginManager;
    }

    public LatLng getCurrLocation() {
        if (myLocation == null) {
            new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        }
        return myLocation;
    }

    public void setCurrLocation(LatLng myLocation) {
        this.myLocation = myLocation;
    }
}