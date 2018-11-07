package com.limefriends.molde.common.fcm;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.limefriends.molde.common.helper.PreferenceUtil;

import static com.limefriends.molde.common.Constant.Common.PREF_KEY_FCM_TOKEN;

public class MoldeFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        PreferenceUtil.putString(getApplicationContext(), PREF_KEY_FCM_TOKEN, refreshedToken);
    }

}
