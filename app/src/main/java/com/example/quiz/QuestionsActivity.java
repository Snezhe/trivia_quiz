package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity implements View.OnClickListener {

    Integer score, score1;
    private DocumentReference documentReference;
    private List<Question> questionList;
    private TextView question, question_counter, lives_counter, timer;
    private Button answer1, answer2, answer3, answer4;
    private int questionNumber, lives = 3, level_id = 1, question_id = 1;
    private CountDownTimer countDownTimer;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        questionNumber = 0;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userID = currentUser.getUid();

        question = findViewById(R.id.questions);
        question_counter = findViewById(R.id.questions_count);
        lives_counter = findViewById(R.id.lives_count);
        timer = findViewById(R.id.timer);

        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        answer4 = findViewById(R.id.answer4);

        answer1.setOnClickListener(this);
        answer2.setOnClickListener(this);
        answer3.setOnClickListener(this);
        answer4.setOnClickListener(this);

        getQuestionsList();
    }

    private void getQuestionsList() {
        questionList = new ArrayList<>();

        firebaseFirestore.collection("Quiz").document("Level" + String.valueOf(level_id)).collection("Questions" + String.valueOf(question_id))
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot questions = task.getResult();

                for (QueryDocumentSnapshot documentSnapshot : questions) {
                    String question = documentSnapshot.get("question").toString();
                    String answer1 = documentSnapshot.get("A").toString();
                    String answer2 = documentSnapshot.get("B").toString();
                    String answer3 = documentSnapshot.get("C").toString();
                    String answer4 = documentSnapshot.get("D").toString();
                    Integer answer = Integer.valueOf(documentSnapshot.get("answer").toString());

                    questionList.add(new Question(question, answer1, answer2, answer3, answer4, answer));
                }
                setQuestion();
            } else {
                Toast.makeText(QuestionsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setQuestion() {
        timer.setText(String.valueOf(20));
        Collections.shuffle(questionList);

        question.setText(questionList.get(questionNumber).getQuestion());
        answer1.setText(questionList.get(questionNumber).getAnswer1());
        answer2.setText(questionList.get(questionNumber).getAnswer2());
        answer3.setText(questionList.get(questionNumber).getAnswer3());
        answer4.setText(questionList.get(questionNumber).getAnswer4());

        question_counter.setText((questionNumber + 1) + "/" + 10);
        lives_counter.setText(String.valueOf(lives));

        startTimer();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(21000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                Intent timesUpIntent = new Intent(QuestionsActivity.this, TimesUpActivity.class);
                timesUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(timesUpIntent);
                QuestionsActivity.this.finish();
            }
        };
        countDownTimer.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        int selectedAnswer = 0;

        switch (v.getId()) {
            case R.id.answer1:
                selectedAnswer = 1;
                break;
            case R.id.answer2:
                selectedAnswer = 2;
                break;
            case R.id.answer3:
                selectedAnswer = 3;
                break;
            case R.id.answer4:
                selectedAnswer = 4;
                break;
            default:
        }
        cancelTimer();
        checkAnswer(selectedAnswer, v);
    }

    private void checkAnswer(int selectedAnswer, View view) {
        if (selectedAnswer == questionList.get(questionNumber).getCorrectAnswer()) {
            documentReference = firebaseFirestore.collection("Users").document(userID);
            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        score = Integer.parseInt(documentSnapshot.get("score").toString());
                        score = score + 5;
                        documentReference.update("score", score);
                    }
                }
            });
            ((Button) view).setBackgroundColor(Color.GREEN);
        } else {
            ((Button) view).setBackgroundColor(Color.RED);

            lives--;
            lives_counter.setText(String.valueOf(lives));
            if (lives == 0) {
                documentReference = firebaseFirestore.collection("Users").document(userID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                score = Integer.parseInt(documentSnapshot.get("score").toString());
                                score = 0;
                                documentReference.update("score", score);
                            }
                        }
                    }
                });
            }

            switch (questionList.get(questionNumber).getCorrectAnswer()) {
                case 1:
                    answer1.setBackgroundColor(Color.GREEN);
                    break;
                case 2:
                    answer2.setBackgroundColor(Color.GREEN);
                    break;
                case 3:
                    answer3.setBackgroundColor(Color.GREEN);
                    break;
                case 4:
                    answer4.setBackgroundColor(Color.GREEN);
                    break;
                default:
            }
        }
        Handler handler = new Handler();
        handler.postDelayed(() -> changeQuestion(), 500);
    }

    private void changeQuestion() {
        if (questionNumber < questionList.size() - 1) {
            questionNumber++;

            playAnimations(question, 0, 0);
            playAnimations(answer1, 0, 1);
            playAnimations(answer2, 0, 2);
            playAnimations(answer3, 0, 3);
            playAnimations(answer4, 0, 4);

            question_counter.setText(questionNumber + 1 + "/" + 10);

            timer.setText(String.valueOf(20));
            startTimer();


            if (lives == 0) {
                Intent gameOverIntent = new Intent(QuestionsActivity.this, GameOverActivity.class);
                gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(gameOverIntent);
                QuestionsActivity.this.finish();
            } else if (questionNumber == 10) {
                question_counter.setText(10 + "/" + 10);
                Intent theEndIntent = new Intent(QuestionsActivity.this, TheEndActivity.class);
                theEndIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(theEndIntent);
                QuestionsActivity.this.finish();
            }
        } else {
            Intent theEndIntent = new Intent(QuestionsActivity.this, TheEndActivity.class);
            theEndIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(theEndIntent);
            QuestionsActivity.this.finish();
        }
    }

    private void playAnimations(final View view, final int value, final int viewNumber) {
        view.animate().alpha(value).scaleY(value).setDuration(400)
                .setStartDelay(70).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (value == 0) {
                            switch (viewNumber) {
                                case 0:
                                    ((TextView) view).setText(questionList.get(questionNumber).getQuestion());
                                    break;
                                case 1:
                                    ((Button) view).setText(questionList.get(questionNumber).getAnswer1());
                                    break;
                                case 2:
                                    ((Button) view).setText(questionList.get(questionNumber).getAnswer2());
                                    break;
                                case 3:
                                    ((Button) view).setText(questionList.get(questionNumber).getAnswer3());
                                    break;
                                case 4:
                                    ((Button) view).setText(questionList.get(questionNumber).getAnswer4());
                                    break;
                                default:
                            }
                            if (viewNumber != 0) {
                                ((Button) view).setBackgroundColor(Color.parseColor("#FF8708"));
                            }
                            playAnimations(view, 1, viewNumber);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

}
