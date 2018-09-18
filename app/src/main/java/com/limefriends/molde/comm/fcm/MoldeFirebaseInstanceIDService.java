package com.limefriends.molde.comm.fcm;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.limefriends.molde.comm.utils.PreferenceUtil;
import com.limefriends.molde.ui.mypage.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;

import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_FCM_TOKEN;
import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_FIRESTORE_TOKEN;

public class MoldeFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("호출확인", refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        PreferenceUtil.putString(getApplicationContext(), PREF_KEY_FCM_TOKEN, refreshedToken);
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshedToken);
        db.collection("token")
                .add(tokenMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // 이 임시 키 값으로 현재 기기를 구분한다.
                        String storeKey = documentReference.getPath().split("/")[1];

                        PreferenceUtil.putString(getBaseContext(), PREF_KEY_FIRESTORE_TOKEN, storeKey);

                        String token = PreferenceUtil.getString(getBaseContext(), PREF_KEY_FIRESTORE_TOKEN);
                        Log.e("호출확인토큰", token);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("createUserData", "Error adding document", e);
                    }
                });
    }

}
