package com.example.quiz;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class QuestionRepository {

    private QuestionDao questionDao;
    private LiveData<List<Question2>> allQuestions;

    QuestionRepository(Application application) {
        QuestionRoomDatabase questionRoomDatabase = QuestionRoomDatabase.getDatabase(application);
        questionDao = questionRoomDatabase.questionDao();
        allQuestions = questionDao.getAllQuestions();
    }

    LiveData<List<Question2>> getAllQuestions() {
        return allQuestions;
    }

    void insert(Question2 question) {
        QuestionRoomDatabase.databaseWriteExecutor.execute(() -> {
            questionDao.insert(question);
        });
    }

}
