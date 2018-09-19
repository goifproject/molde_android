package com.limefriends.molde.ui.mypage.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.limefriends.molde.R;
import com.limefriends.molde.comm.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.comm.Constant.Common.PREF_KEY_FCM_TOKEN;

public class LoginActivity extends AppCompatActivity {

    public static final int NORMAL = 0;

    @BindView(R.id.login_google_button)
    RelativeLayout login_google_button;
    @BindView(R.id.login_facebook_button)
    RelativeLayout login_facebook_button;
    @BindView(R.id.login_to_google)
    TextView login_to_google;
    @BindView(R.id.login_to_facebook)
    TextView login_to_facebook;
    @BindView(R.id.skip_login_button)
    TextView skip_login_button;

    ProgressDialog loginProgressDialog;

    //로그인 안하고 건너뜀
    private static final int SKIP_LOGIN_CODE = 1001;
    //구글 로그인 완료
    private static final int CONNECT_GOOGLE_AUTH_CODE = 1002;
    //페북 로그인 완료
    private static final int CONNECT_FACEBOOK_AUTH_CODE = 1003;
    //파이어베이스 인증 클라이언트
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Handler mHandler = new Handler();
    private GoogleSignInClient googleSignInClient;
    private CallbackManager facebookCallbackManager;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupViews();

        setupListener();

        configureFacebookSignIn();

        configureGoogleSignIn();
    }

    private void setupViews() {
        ButterKnife.bind(this);
        loginProgressDialog = new ProgressDialog(LoginActivity.this);
        loginProgressDialog.setTitle("로그인 중입니다...");
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

    // --------
    // 구글 로그인
    // --------

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

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        // TODO 토큰값 변경 반영 위해 리스너로 설정해야 할 듯
        loginProgressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            String token = PreferenceUtil.getString(LoginActivity.this, PREF_KEY_FCM_TOKEN);
                            // 사용자 데이터 추가
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("uId", user.getUid());
                            userMap.put("token", token);
                            userMap.put("authority", NORMAL);
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                createUserData(userMap, user.getUid(), CONNECT_GOOGLE_AUTH_CODE);
                            } else {
                                loadUserData(user.getUid(), CONNECT_GOOGLE_AUTH_CODE);
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

    // -----------
    // 페이스북 로그인
    // -----------

    private void configureFacebookSignIn() {
        facebookCallbackManager = CallbackManager.Factory.create();
        final LoginButton facebookLoginButton = findViewById(R.id.in_facebook_login_button);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
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

    private void handleFacebookAccessToken(final AccessToken token) {
        loginProgressDialog.show();
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                String fcmToken = PreferenceUtil.getString(LoginActivity.this, PREF_KEY_FCM_TOKEN);
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("uId", user.getUid());
                                userMap.put("token", fcmToken);
                                userMap.put("authority", NORMAL);
                                createUserData(userMap, user.getUid(), CONNECT_FACEBOOK_AUTH_CODE);
                            } else {
                                loadUserData(user.getUid(), CONNECT_FACEBOOK_AUTH_CODE);
                            }
                        } else {
                            // TODO 기존에 존재하는 아이디일 경우
                            loginProgressDialog.dismiss();
                            Log.e("facebook", task.getException().getMessage());
                            if (task.getException().getMessage()
                                    .startsWith("An account already exists with the same email address")) {
                                Snackbar.make(findViewById(R.id.mypage_login_layout),
                                        "이미 가입된 아이디입니다", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(findViewById(R.id.mypage_login_layout),
                                        "로그인이 정상적으로 처리되지 않았습니다.", Snackbar.LENGTH_SHORT).show();
                            }

                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    LoginManager.getInstance().logOut();
                                    finish();
                                }
                            }, 1000);

                            Log.e("facebook", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    // --------
    // 로그인 공통
    // --------

    private void createUserData(final Map<String, Object> userMap, String uId, final int resultCode) {
        db.collection("users").document(uId)
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // TODO 실패시 롤백
                        // 권한은 Preference에 저장, 나머지는 FirebaseAuth 를 통해 얻어오고,
                        // 리스너 설정해서 변화가 일어날 때마다 다시 데이터 받아오고 갱신해 준다.
                        int authority = (int) userMap.get("authority");
                        PreferenceUtil.putLong(LoginActivity.this, "authority", authority);
                        loginProgressDialog.dismiss();
                        setResult(resultCode);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("createUserData", "Error adding document", e);
                    }
                });
    }

    private void loadUserData(final String uId, final int resultCode) {
        db.collection("users").document(uId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String uuId = (String) documentSnapshot.get("uId");
                        String token =
                                PreferenceUtil.getString(LoginActivity.this, PREF_KEY_FCM_TOKEN);
                        long authority = (long) documentSnapshot.get("authority");

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("uId", uuId);
                        userMap.put("token", token);
                        userMap.put("authority", authority);

                        refreshFcmToken(uuId, userMap);

                        PreferenceUtil.putLong(LoginActivity.this,
                                "authority", authority);
                        loginProgressDialog.dismiss();
                        setResult(resultCode);
                        finish();
                    }
                });
    }

    private void refreshFcmToken(String uId, Map<String, Object> userMap) {
        db.collection("users").document(uId).set(userMap);
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

}

