package com.example.quiz;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions_table")
public class Question2 {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int question_id;

    public int image;
    public String theQuestion, A, B, C, D, rightAnswer;

    public Question2(int image, String theQuestion, String A, String B, String C, String D, String rightAnswer) {
        this.image = image;
        this.theQuestion = theQuestion;
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        this.rightAnswer = rightAnswer;
    }

    public int getImage() {
        return image;
    }

    public String getTheQuestion() {
        return theQuestion;
    }

    public String getA() {
        return A;
    }

    public String getB() {
        return B;
    }

    public String getC() {
        return C;
    }

    public String getD() {
        return D;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

}
