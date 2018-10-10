package com.limefriends.molde.comm;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.limefriends.molde.comm.utils.PreferenceUtil;

import io.fabric.sdk.android.Fabric;

import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_FIRESTORE_TOKEN;

// TODO 로그인 매니저 설정해줘야 함
public class MoldeApplication extends Application {

    private static final double DEFAULT_LAT = 37.5662952;
    private static final double DEFAULT_LNG = 126.97794509999994;

    private FirebaseAuth firebaseAuth;
    private LatLng myLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    public FirebaseAuth getFireBaseAuth() {
        return firebaseAuth;
    }

    public LatLng getCurrLocation() {
        if (myLocation == null) {
            myLocation = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        }
        return myLocation;
    }

    public void setCurrLocation(LatLng myLocation) {
        this.myLocation = myLocation;
    }
}
