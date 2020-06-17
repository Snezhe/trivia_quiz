package com.example.quiz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.example.quiz.SignUpActivity.TAG1;
import static com.facebook.FacebookSdk.setAutoLogAppEventsEnabled;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EMAIL = "email";
    private AlertDialog.Builder dialog;
    private String userID;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Integer score;
    private Intent welcomeIntent, loginIntent, signUpIntent, googleSignInIntent;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String TAG = "MainActivity";
    private AccessTokenTracker accessTokenTracker;
    private Button signInButton, googleSignInButton, guestLoginButton, fbLoginButton;
    private TextView signUpTextView;
    private GoogleSignInClient googleSignInClient;
    private int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    private CallbackManager callbackManager;
    private FirebaseUser currentUser;
    private long backPressedTime;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.signIn_button);
        signUpTextView = findViewById(R.id.textView_signUp);
        googleSignInButton = findViewById(R.id.login_Google_button);
        fbLoginButton = findViewById(R.id.fb_login_button);
        guestLoginButton = findViewById(R.id.login_guest_button);

        callbackManager = CallbackManager.Factory.create();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setAutoLogAppEventsEnabled(true);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "method");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);


        Log.d("Token", " " + FirebaseInstanceId.getInstance().getToken());
        FirebaseMessaging.getInstance().subscribeToTopic("allDevices");

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Success: " + loginResult);
                handlerFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Error: " + error);
            }
        });

        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                updateUI(user);
            } else {
                updateUI(null);
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                }
            }
        };

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleSignInButton.setOnClickListener(v -> googleSignIn());

        guestLoginButton.setOnClickListener(v -> guestLogin());

        signInButton.setOnClickListener(v -> {
            loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        });

        signUpTextView.setOnClickListener(v -> {
            signUpIntent = new Intent(MainActivity.this, SignUpActivity.class);
            signUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signUpIntent);
        });
    }

    private void handlerFacebookToken(AccessToken accessToken) {
        Log.d(TAG, "Handler Facebook Token" + accessToken);
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                userID = currentUser.getUid();
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                final Map<String, Object> user = new HashMap<>();
                user.put("username", currentUser.getDisplayName());
                user.put("email", currentUser.getEmail());
                user.put("pass", null);
                user.put("score", 0);
                documentReference.set(user).addOnSuccessListener(aVoid -> Log.d(TAG1, "onSuccess: User profile is created for" + userID));
                updateUI(currentUser);
            } else {
                Log.d(TAG, "Not signed in");
                updateUI(null);
            }
        });
    }

    public void onClick(View view) {
        if (view == fbLoginButton) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos", "email", "user_birthday", "public_profile"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void googleSignIn() {
        googleSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            FirebaseGoogleAuth(account);
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Signed In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                userID = currentUser.getUid();
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("username", currentUser.getDisplayName());
                user.put("email", currentUser.getEmail());
                user.put("pass", null);
                user.put("score", 0);
                documentReference.set(user).addOnSuccessListener(aVoid -> Log.d(TAG1, "onSuccess: User profile is created for" + userID));
                updateUI(currentUser);
            } else {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void guestLogin() {
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                userID = currentUser.getUid();
                final int random = new Random().nextInt(1000000) + 1;
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("username", "Guest" + String.valueOf(random));
                user.put("email", currentUser.getEmail());
                user.put("pass", null);
                user.put("score", 0);
                documentReference.set(user).addOnSuccessListener(aVoid -> Log.d(TAG1, "onSuccess: User profile is created for" + userID));
                updateUI(currentUser);
            } else {
                Toast.makeText(MainActivity.this, "Not Successful", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (currentUser != null) {
            finishActivity();
            welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcomeIntent);
        } /* else if (currentUser != null) {
            welcomeIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcomeIntent);
        } */
    }

    public void finishActivity() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void openSettings(View view) {
        View mView = getLayoutInflater().inflate(R.layout.settings_dialog, null);
        ImageButton mkButton = mView.findViewById(R.id.mk);
        ImageButton enButton = mView.findViewById(R.id.en);
        dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.settings))
                .setNegativeButton("GO BACK", (dialog, which) -> dialog.dismiss());
        mkButton.setOnClickListener(v -> {
            Configuration conf = getResources().getConfiguration();
            conf.locale = new Locale("mk");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Resources resources = new Resources(getAssets(), metrics, conf);
            recreate();
        });
        enButton.setOnClickListener(v -> {
            Configuration conf = getResources().getConfiguration();
            conf.locale = new Locale("en");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            Resources resources = new Resources(getAssets(), metrics, conf);
            recreate();
        });
        dialog.setView(mView);
        dialog.create().show();
    }

}