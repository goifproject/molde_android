package com.limefriends.molde.common.app;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.limefriends.molde.common.di.CompositionRoot;

import io.fabric.sdk.android.Fabric;

public class MoldeApplication extends Application {

    private static final double DEFAULT_LAT = 37.5662952;
    private static final double DEFAULT_LNG = 126.97794509999994;

    private FirebaseAuth firebaseAuth;
    private LatLng myLocation;
    private CompositionRoot mCompositionRoot;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        mCompositionRoot = new CompositionRoot();
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

    public CompositionRoot getCompositionRoot() {
        return mCompositionRoot;
    }
}
