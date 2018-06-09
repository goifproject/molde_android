package com.limefriends.molde.menu_mypage;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.limefriends.molde.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoldeMypageLoginActivity extends AppCompatActivity {
    @BindView(R.id.login_google_button)
    RelativeLayout login_google_button;
    @BindView(R.id.login_to_google)
    TextView login_to_google;
    @BindView(R.id.login_to_facebook)
    TextView login_to_facebook;
    @BindView(R.id.skip_login_button)
    Button skip_login_button;

    //파이어베이스 인증 클라이언트
    private static final int RC_SIGN_IN = 9001;

    // 파이어 베이스 계정
    public static FirebaseAuth firebaseAuth;

    //GoogleSignClient
    public GoogleSignInClient googleSignInClient;
    //FacebookSignClient TODO Facebook Client 연동 구현 - 완료
    CallbackManager facebookCallbackManager;
    AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
    boolean fbIsLoggedIn = fbAccessToken != null && !fbAccessToken.isExpired();

    ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_login);
        ButterKnife.bind(this);

        loginProgressDialog = new ProgressDialog(MoldeMypageLoginActivity.this);
        loginProgressDialog.setTitle("로그인 중입니다...");

        // Configure Facebook Init
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        facebookCallbackManager = CallbackManager.Factory.create();
        if(fbIsLoggedIn){
            login_to_facebook.setText("로그아웃 하기");
        }
        final LoginButton facebookLoginButton = findViewById(R.id.in_facebook_login_button);
        facebookLoginButton.setReadPermissions("email", "public_profile");
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login_to_facebook.getText().equals("로그아웃 하기")){
                    firebaseAuth.signOut();
                }
            }
        });
        facebookLoginButton.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult facebookLoginResult) {
                Log.e("facebook", "facebook:onSuccess:" + facebookLoginResult);
                handleFacebookAccessToken(facebookLoginResult.getAccessToken());
                login_to_facebook.setText("로그아웃 하기");
            }

            @Override
            public void onCancel() {
                Log.e("facebook", "facebook:onCancel");
                login_to_facebook.setText("페이스북으로 로그인하기");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("facebook", "facebook:onError", error);
                login_to_facebook.setText("페이스북으로 로그인하기");
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // [START initialize_auth]
        firebaseAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]


        login_google_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login_to_google.getText().equals("로그아웃 하기")) {
                    googleSignOut();
                } else if (login_to_google.getText().equals("구글로 로그인하기")) {
                    googleSignIn();
                }
            }
        });

        skip_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        googleUpdateAuth(null);
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
                        googleUpdateAuth(null);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // [START on_start_check_user]
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        googleUpdateAuth(currentUser);
    }

    private void googleUpdateAuth(FirebaseUser user) {
        if (user != null) {
            login_to_google.setText("로그아웃 하기");
        } else {
            login_to_google.setText("구글로 로그인하기");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("google", "Google sign in failed", e);
                // [START_EXCLUDE]
                googleUpdateAuth(null);
                // [END_EXCLUDE]
            }
        }
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("google", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        loginProgressDialog.show();
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("google", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            googleUpdateAuth(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("google", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.mypage_login_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            googleUpdateAuth(null);
                        }

                        // [START_EXCLUDE]
                        loginProgressDialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e("facebook", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("facebook", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            googleUpdateAuth(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("facebook", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            googleUpdateAuth(null);
                        }

                    }
                });
    }
}
