package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "com.example.quiz.EXTRA_TEXT";
    public static final String TAG1 = "TAG";
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private EditText signUpEmail, signUpPass, signUpUsername;
    private Button signUpButton;
    private Intent welcomeIntent;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        mAuth = FirebaseAuth.getInstance();

        signUpUsername = findViewById(R.id.signUp_username);
        signUpEmail = findViewById(R.id.signUp_email);
        signUpPass = findViewById(R.id.signUp_password);
        signUpButton = findViewById(R.id.signUp_button);
        progressBar = findViewById(R.id.progress_bar3);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

    }

    private void signUp() {
        final String username = signUpUsername.getText().toString();
        final String email = signUpEmail.getText().toString().trim();
        final String password = signUpPass.getText().toString().trim();

        if (email.isEmpty()) {
            signUpEmail.setError("Email is required");
            signUpEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            signUpEmail.setError("Please enter a valid email.");
            signUpEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            signUpPass.setError("Password is required.");
            signUpPass.requestFocus();
            return;
        }

        if (password.length() < 6) {
            signUpPass.setError("Your password must be at least 6 characters long. Please try another.");
            signUpPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    currentUser = mAuth.getCurrentUser();
                    userID = currentUser.getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", email);
                    user.put("pass", password);
                    user.put("score", 0);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG1, "onSuccess: User profile is created for" + userID);
                        }
                    });
                    checkUser(currentUser);
                } else {
                    Toast.makeText(SignUpActivity.this, "Not registered", Toast.LENGTH_LONG).show();
                    checkUser(null);
                }
            }
        });
    }

    private void checkUser(FirebaseUser user) {
        String username = signUpUsername.getText().toString();
        if (currentUser != null) {
            welcomeIntent = new Intent(SignUpActivity.this, WelcomeActivity.class);
            welcomeIntent.putExtra(EXTRA_TEXT, username);
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcomeIntent);
            Log.d(TAG, "Logged in");
        } else {
            Log.d(TAG, "Not logged in");
        }
    }

}