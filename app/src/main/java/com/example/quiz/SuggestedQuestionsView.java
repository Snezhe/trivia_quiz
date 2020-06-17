package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SuggestedQuestionsView extends AppCompatActivity {

    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;
    private QuestionViewModel questionViewModel;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_questions_view);

        title = findViewById(R.id.title_SQ);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        title.setTypeface(typeface);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final QuestionListAdapter adapter = new QuestionListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(SuggestedQuestionsView.this, SuggestAQuestion.class);
            startActivityForResult(intent, NEW_WORD_ACTIVITY_REQUEST_CODE);
        });

        questionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);

        questionViewModel.getAllQuestions().observe(this, question2s -> adapter.setQuestionsList(question2s));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_WORD_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Question2 word = new Question2(R.drawable.trivia3, data.getStringExtra(SuggestAQuestion.EXTRA_REPLY), data.getStringExtra(SuggestAQuestion.EXTRA_REPLY1), data.getStringExtra(SuggestAQuestion.EXTRA_REPLY2),
                    data.getStringExtra(SuggestAQuestion.EXTRA_REPLY3), data.getStringExtra(SuggestAQuestion.EXTRA_REPLY4), data.getStringExtra(SuggestAQuestion.EXTRA_REPLY5));
            questionViewModel.insert(word);
        } else {
            Log.d("TAG", "Not saved!");
        }
    }

}