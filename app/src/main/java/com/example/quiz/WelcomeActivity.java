package com.example.quiz;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    String userID;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Integer score, count = 0;
    AlertDialog.Builder dialog;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private TextView username, title, suggestQuestion;
    private ImageView profilePic, openSettings, openFragment;
    private Intent logoutIntent;
    private Button playButton, worldRank;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        username = findViewById(R.id.username);
        profilePic = findViewById(R.id.profilePic);
        title = findViewById(R.id.trivia_title);
        playButton = findViewById(R.id.start_game_button);
        worldRank = findViewById(R.id.world_rank_button);
        suggestQuestion = findViewById(R.id.suggest_question);
        openSettings = findViewById(R.id.openSettings_Button2);
        openFragment = findViewById(R.id.openFragment);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "ostrich-regular.ttf");
        title.setTypeface(typeface);

        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        username.setTypeface(typeface2);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        if (firebaseUser != null) {
            username.setText(firebaseUser.getDisplayName());
            if (firebaseUser.getPhotoUrl() != null) {
                String photoUrl = firebaseUser.getPhotoUrl().toString();
                photoUrl = photoUrl + "?type=large";
                Picasso.get().load(photoUrl).transform(new CropCircleTransformation()).into(profilePic);
            } else {
                final DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
                documentReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("username");
                            username.setText(userName);
                        }
                    }
                });
                profilePic.setImageResource(R.drawable.robot);
            }
        }

        worldRank.setOnClickListener(v -> {
            Intent worldRank = new Intent(WelcomeActivity.this, ScoreActivity.class);
            worldRank.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(worldRank);
        });

        playButton.setOnClickListener(v -> {
            Intent startGame = new Intent(WelcomeActivity.this, QuestionsActivity.class);
            startGame.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startGame);
        });

        suggestQuestion.setOnClickListener(v -> {
            Intent suggest = new Intent(WelcomeActivity.this, SuggestedQuestionsView.class);
            suggest.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(suggest);
        });

        openFragment.setOnClickListener(v -> {
            showHideFragment();
        });

    }

    public void openSettings2(View view) {
        View mView = getLayoutInflater().inflate(R.layout.settings_dialog2, null);
        ImageButton mkButton = mView.findViewById(R.id.mk);
        ImageButton enButton = mView.findViewById(R.id.en);
        ImageButton logoutButton = mView.findViewById(R.id.logout_button);
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
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            logoutIntent = new Intent(WelcomeActivity.this, MainActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutIntent);
        });
        dialog.setView(mView);
        dialog.create().show();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void showHideFragment() {
        if (count == 0) {
            count++;
            Fragment1 fragment1 = new Fragment1();
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .add(R.id.container, fragment1, "F1")
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(fragment1)
                    .commit();
        } else {
            Fragment fm = getFragmentManager().findFragmentByTag("F1");
            if (fm.isVisible() && fm != null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                ft.detach(fm);
                ft.commit();
            } else {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                ft.attach(fm);
                ft.commit();
            }
        }
    }

}
