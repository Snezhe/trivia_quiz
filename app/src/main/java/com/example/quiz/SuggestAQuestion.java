package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SuggestAQuestion extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.quiz.REPLY";
    public static final String EXTRA_REPLY1 = "com.example.quiz.REPLY1";
    public static final String EXTRA_REPLY2 = "com.example.quiz.REPLY2";
    public static final String EXTRA_REPLY3 = "com.example.quiz.REPLY3";
    public static final String EXTRA_REPLY4 = "com.example.quiz.REPLY4";
    public static final String EXTRA_REPLY5 = "com.example.quiz.REPLY5";


    private EditText question, answer1, answer2, answer3, answer4, rightAnswer;
    private TextView questionTitle, answerTitle, rAnswerTitle;
    private Button suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_a_question);

        suggest = findViewById(R.id.suggest_button);

        question = findViewById(R.id.enter_question_editText);
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);
        rightAnswer = findViewById(R.id.enter_right_answer_editText);
        questionTitle = findViewById(R.id.enter_question_title);
        answerTitle = findViewById(R.id.enter_answers_title);
        rAnswerTitle = findViewById(R.id.enter_right_answer);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        questionTitle.setTypeface(typeface);
        answerTitle.setTypeface(typeface);
        rAnswerTitle.setTypeface(typeface);

        suggest.setOnClickListener(v -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(question.getText()) && TextUtils.isEmpty(answer1.getText()) && TextUtils.isEmpty(answer2.getText())
                    && TextUtils.isEmpty(answer3.getText()) && TextUtils.isEmpty(answer4.getText()) && TextUtils.isEmpty(rightAnswer.getText())) {
                setResult(Activity.RESULT_CANCELED, replyIntent);
            } else {
                String questionString = question.getText().toString();
                String A = answer1.getText().toString();
                String B = answer2.getText().toString();
                String C = answer3.getText().toString();
                String D = answer4.getText().toString();
                String rightAnswerString = rightAnswer.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, questionString);
                replyIntent.putExtra(EXTRA_REPLY1, A);
                replyIntent.putExtra(EXTRA_REPLY2, B);
                replyIntent.putExtra(EXTRA_REPLY3, C);
                replyIntent.putExtra(EXTRA_REPLY4, D);
                replyIntent.putExtra(EXTRA_REPLY5, rightAnswerString);
                setResult(Activity.RESULT_OK, replyIntent);
            }
            finish();
        });
    }

}