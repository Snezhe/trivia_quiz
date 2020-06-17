package com.example.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_TEXT = "com.example.quiz.EXTRA_TEXT";
    private static final String TAG = LoginActivity.class.getSimpleName();
    EditText loginEmail, loginPass;
    Button loginButton;
    FirebaseUser currentUser;
    Intent welcomeIntent;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.signIn_email);
        loginPass = findViewById(R.id.signIn_password);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar2);

        loginButton.setOnClickListener(v -> updateUI());
    }

    private void updateUI() {
        String email = loginEmail.getText().toString();
        String password = loginPass.getText().toString();
        if (email.isEmpty()) {
            loginEmail.setError("Email is required");
            loginEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmail.setError("Please enter a valid email.");
            loginEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            loginPass.setError("Password is required.");
            loginPass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                currentUser = mAuth.getCurrentUser();
                checkUser(currentUser);
            } else {
                checkUser(null);
            }
        });
    }

    private void checkUser(FirebaseUser user) {
        if (currentUser != null) {
            Log.d(TAG, "Logged in");
            welcomeIntent = new Intent(LoginActivity.this, WelcomeActivity.class);
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcomeIntent);
        } else {
            Log.d(TAG, "Not logged in");
        }
    }

}
