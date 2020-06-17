package com.example.quiz;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class QuestionViewModel extends AndroidViewModel {

    private QuestionRepository questionRepository;
    private LiveData<List<Question2>> allQuestions;

    public QuestionViewModel(Application application) {
        super(application);
        questionRepository = new QuestionRepository(application);
        allQuestions = questionRepository.getAllQuestions();
    }

    LiveData<List<Question2>> getAllQuestions() {
        return allQuestions;
    }

    public void insert(Question2 question) {
        questionRepository.insert(question);
    }

}
