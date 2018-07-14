package com.limefriends.molde.menu_mypage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.limefriends.molde.MoldeApplication;
import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.limefriends.molde.MoldeApplication.fbLoginManager;
import static com.limefriends.molde.MoldeApplication.firebaseAuth;
import static com.limefriends.molde.MoldeApplication.ggClient;

public class MoldeMyPageLoginActivity extends AppCompatActivity {
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
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        loginProgressDialog = new ProgressDialog(MoldeMyPageLoginActivity.this);
        loginProgressDialog.setTitle("로그인 중입니다...");

        // 페이스북 초기화
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        facebookCallbackManager = CallbackManager.Factory.create();
        final LoginButton facebookLoginButton = findViewById(R.id.in_facebook_login_button);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult facebookLoginResult) {
                Log.e("facebook", "facebook:onSuccess:" + facebookLoginResult);
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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        login_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        skip_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SKIP_LOGIN_CODE);
                finish();
            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("google", "firebaseAuthWithGoogle:" + acct.getId());
        loginProgressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            MoldeApplication.firebaseAuth = FirebaseAuth.getInstance();
                            Log.e("google", "signInWithCredential:success");
                            loginProgressDialog.dismiss();
                            setResult(CONNECT_GOOGLE_AUTH_CODE);
                            finish();
                        } else {
                            Log.e("google", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.mypage_login_layout), "로그인이 정상적으로 처리되지 않았습니다.", Snackbar.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e("facebook", "handleFacebookAccessToken:" + token);
        loginProgressDialog.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            MoldeApplication.firebaseAuth = FirebaseAuth.getInstance();
                            Log.e("facebook", "signInWithCredential:success");
                            loginProgressDialog.dismiss();
                            setResult(CONNECT_FACEBOOK_AUTH_CODE);
                            finish();
                        } else {
                            Log.e("facebook", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.mypage_login_layout), "로그인이 정상적으로 처리되지 않았습니다.", Snackbar.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
