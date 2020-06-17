package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class GameOverActivity extends AppCompatActivity {

    String userID;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    TextView title, scoreTitle;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private ImageView profilePic;
    private Button tryAgain, worldRank, menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        profilePic = findViewById(R.id.profilePic);
        title = findViewById(R.id.game_over);
        scoreTitle = findViewById(R.id.score);
        tryAgain = findViewById(R.id.try_again_button);
        worldRank = findViewById(R.id.world_rank_button);
        menu = findViewById(R.id.menu);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        title.setTypeface(typeface);
        scoreTitle.setTypeface(typeface);

        if (firebaseUser != null) {
            final DocumentReference documentReference = firebaseFirestore.collection("Users").document(userID);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Long score = documentSnapshot.getLong("score");
                        String scoreText = (getString(R.string.score));
                        scoreTitle.setText(scoreText + " " + score.toString());
                    }
                }
            });
            if (firebaseUser.getPhotoUrl() != null) {
                String photoUrl = firebaseUser.getPhotoUrl().toString();
                photoUrl = photoUrl + "?type=large";
                Picasso.get().load(photoUrl).transform(new CropCircleTransformation()).into(profilePic);
            } else {
                profilePic.setImageResource(R.drawable.robot);
            }
        }

        tryAgain.setOnClickListener(v -> {
            Intent startGame = new Intent(GameOverActivity.this, QuestionsActivity.class);
            startGame.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startGame);
        });

        worldRank.setOnClickListener(v -> {
            Intent worldRank = new Intent(GameOverActivity.this, ScoreActivity.class);
            worldRank.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(worldRank);
        });

        menu.setOnClickListener(v -> {
            Intent welcomeIntent = new Intent(GameOverActivity.this, WelcomeActivity.class);
            welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(welcomeIntent);
        });

    }

}
