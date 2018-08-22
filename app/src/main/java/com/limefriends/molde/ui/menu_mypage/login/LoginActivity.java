package com.limefriends.molde.ui.menu_mypage.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.limefriends.molde.comm.MoldeApplication;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    public static final int NORMAL = 0;
    public static final int GREEN = 1;
    public static final int ADMIN = 2;

    @BindView(R.id.login_google_button)
    RelativeLayout login_google_button;
    @BindView(R.id.login_facebook_button)
    RelativeLayout login_facebook_button;
    @BindView(R.id.login_to_google)
    TextView login_to_google;
    @BindView(R.id.login_to_facebook)
    TextView login_to_facebook;
    @BindView(R.id.skip_login_button)
    Button skip_login_button;

    //로그인 안하고 건너뜀
    private static final int SKIP_LOGIN_CODE = 1001;
    //구글 로그인 완료
    private static final int CONNECT_GOOGLE_AUTH_CODE = 1002;
    //페북 로그인 완료
    private static final int CONNECT_FACEBOOK_AUTH_CODE = 1003;

    //파이어베이스 인증 클라이언트
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    /**
     * FirebaseAuth.getInstance() 하는 곳에서 받아 사용한다.
     */
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
            }
            // ...
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }


    //GoogleSignClient
    private GoogleSignInClient googleSignInClient;
    //FacebookSignClient
    CallbackManager facebookCallbackManager;

    ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_login_activity);

        ButterKnife.bind(this);

        loginProgressDialog = new ProgressDialog(LoginActivity.this);
        loginProgressDialog.setTitle("로그인 중입니다...");

        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        configureFacebookSignIn();

        configureGoogleSignIn();

        setupListener();
    }

    private void setupListener() {

        // 구글 로그인
        login_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 하기 전에 클라이언트가 준비되어 있어야 함
                googleSignIn();
            }
        });

        // 로그인 스킵
        skip_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SKIP_LOGIN_CODE);
                finish();
            }
        });
    }

    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void googleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignOut() {
        // Firebase sign out
        firebaseAuth.signOut();
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    //계정 권한 삭제
    private void googleRevokeAccess() {
        // Firebase sign out
        firebaseAuth.signOut();
        // Google revoke access
        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    private void configureFacebookSignIn() {
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

        facebookCallbackManager = CallbackManager.Factory.create();
        final LoginButton facebookLoginButton = findViewById(R.id.in_facebook_login_button);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult facebookLoginResult) {
                Log.e("facebook", "facebook:onSuccess:" + facebookLoginResult);
                Log.e("facebook", "facebook:onSuccess:" + facebookLoginResult.getAccessToken());
                handleFacebookAccessToken(facebookLoginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e("facebook", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("facebook", "facebook:onError", error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO 계정을 삭제했는데 왜 계속 하나밖에 안 뜨는거지
        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.e("google", "Google sign in failed", e);
            }
            return;
        }
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        // TODO 토큰값 변경 반영 위해 리스너로 설정해야 할 듯

        Log.e("google", "firebaseAuthWithGoogle:" + acct.getId());
        loginProgressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();

                            Log.e("user uid", user.getUid());
                            Log.e("user token", user.getIdToken(true) + "");
                            Log.e("acct token", acct.getIdToken());
                            Log.e("acct id", acct.getId());


                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                                Log.e("새로운 사용자", "새로운 사용자");

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("uId", user.getUid());
                                userMap.put("token", acct.getIdToken());
                                userMap.put("authority", NORMAL);

                                // 사용자 데이터 추가
                                createUserData(userMap, user.getEmail());


                            } else {

                                loadUserData(user.getEmail());

//                                ((MoldeApplication) getApplication()).setFireBaseAuth(FirebaseAuth.getInstance());
//                                Log.e("google", "signInWithCredential:success");
//                                loginProgressDialog.dismiss();
//
//                                setResult(CONNECT_GOOGLE_AUTH_CODE);
//                                finish();
                            }


                        } else {

                            // TODO 시도해보자
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                            } else {

                            }

                            // TODO 기존에 존재하는 아이디일 경우
                            Log.e("facebook", task.getException().getMessage());
                            if (task.getException().getMessage().startsWith("An account already exists with the same email address")) {
                                Log.e("facebook", "이미 있어");
                            }

                            Log.e("facebook", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.mypage_login_layout), "로그인이 정상적으로 처리되지 않았습니다.", Snackbar.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void createUserData(final Map<String, Object> userMap, String email) {

//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);

        // Add a new document with a generated ID
        db.collection("users").document(email)
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.e("createUserData", "DocumentSnapshot added with ID: " + documentReference.getId());

                        // TODO 실패시 롤백

                        // 권한은 Preference에 저장, 나머지는 FirebaseAuth 를 통해 얻어오고,
                        // 리스너 설정해서 변화가 일어날 때마다 다시 데이터 받아오고 갱신해 준다.
                        PreferenceUtil.putLong(LoginActivity.this, "authority", (long) userMap.get("authority"));

                        loginProgressDialog.dismiss();
                        setResult(CONNECT_GOOGLE_AUTH_CODE);
                        finish();
                    }
                })
                //.add(userMap)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.e("createUserData", "DocumentSnapshot added with ID: " + documentReference.getId());
//
//                        // TODO 실패시 롤백
//
//                        // 권한은 Preference에 저장, 나머지는 FirebaseAuth 를 통해 얻어오고,
//                        // 리스너 설정해서 변화가 일어날 때마다 다시 데이터 받아오고 갱신해 준다.
//                        PreferenceUtil.putInt(LoginActivity.this, "authority", (int) userMap.get("authority"));
//
//                        loginProgressDialog.dismiss();
//                        setResult(CONNECT_GOOGLE_AUTH_CODE);
//                        finish();
//                    }
//                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("createUserData", "Error adding document", e);
                    }
                });

    }

    private void loadUserData(String email) {

        // uid

        db.collection("users").document(email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.e("loadUserData", String.valueOf(documentSnapshot.get("authority")));

                        //
                        PreferenceUtil.putLong(LoginActivity.this, "authority", (long) documentSnapshot.get("authority"));

                        loginProgressDialog.dismiss();
                        setResult(CONNECT_GOOGLE_AUTH_CODE);
                        finish();


                    }
                })
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                // Log.d(TAG, document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            // Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
        ;

    }


    // TODO 코드 중복
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.e("facebook", "handleFacebookAccessToken:" + token);
        loginProgressDialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

//                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
//
//                        } else {
//
//                        }

                        if (task.isSuccessful()) {

                            FirebaseUser user = task.getResult().getUser();

                            Log.e("user uid", user.getUid());
                            Log.e("user token", user.getIdToken(true) + "");
                            Log.e("token", token.getToken());
                            Log.e("token", token.getUserId());


                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                                Log.e("새로운 사용자", "새로운 사용자");

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("uId", user.getUid());
                                userMap.put("token", token.getToken());
                                userMap.put("authority", NORMAL);

                                // 사용자 데이터 추가
                                createUserData(userMap, user.getEmail());


                            } else {

                                loadUserData(user.getEmail());

//                                ((MoldeApplication) getApplication()).setFireBaseAuth(FirebaseAuth.getInstance());
//                                Log.e("google", "signInWithCredential:success");
//                                loginProgressDialog.dismiss();
//                                setResult(CONNECT_GOOGLE_AUTH_CODE);
//                                finish();
                            }

                        } else {


                            // TODO 시도해보자
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                            } else {

                            }

                            // TODO 기존에 존재하는 아이디일 경우
                            Log.e("facebook", task.getException().getMessage());
                            if (task.getException().getMessage().startsWith("An account already exists with the same email address")) {
                                Log.e("facebook", "이미 있어");
                            }

                            Log.e("facebook", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.mypage_login_layout), "로그인이 정상적으로 처리되지 않았습니다.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}

