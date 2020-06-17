package com.example.quiz;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Question2 question);

    @Query("DELETE FROM questions_table")
    void deleteAll();

    @Query("SELECT * FROM questions_table")
    LiveData<List<Question2>> getAllQuestions();

}
