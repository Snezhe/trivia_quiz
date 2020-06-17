package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ScoreActivity extends AppCompatActivity {

    private RecyclerView firestoreList;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firestoreList = findViewById(R.id.recyclerview_scores);

        TextView title = (TextView) findViewById(R.id.scoreBoardTitle);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "sea.ttf");
        title.setTypeface(typeface);

        Query query = firebaseFirestore.collection("Users").orderBy("score", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ScoresModel> options = new FirestoreRecyclerOptions.Builder<ScoresModel>()
                .setQuery(query, ScoresModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ScoresModel, ScoresViewHolder>(options) {
            @NonNull
            @Override
            public ScoresViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.score_list_item_single, parent, false);
                return new ScoresViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ScoresViewHolder holder, int position, @NonNull ScoresModel model) {
                holder.position.setText(String.valueOf(position + 1));
                holder.username.setText(model.getUsername());
                holder.scores.setText(String.valueOf(model.getScore()));
            }
        };
        firestoreList.setHasFixedSize(true);
        firestoreList.setLayoutManager(new LinearLayoutManager(this));
        firestoreList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private class ScoresViewHolder extends RecyclerView.ViewHolder {

        private TextView username, scores, position;

        public ScoresViewHolder(@NonNull View itemView) {
            super(itemView);

            position = itemView.findViewById(R.id.position);
            username = itemView.findViewById(R.id.username);
            scores = itemView.findViewById(R.id.score);

            Typeface typeface2 = Typeface.createFromAsset(getAssets(), "OpenSans-Semibold.ttf");
            username.setTypeface(typeface2);
        }

    }

}
