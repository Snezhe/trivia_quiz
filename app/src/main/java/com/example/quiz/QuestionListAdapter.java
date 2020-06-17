package com.example.quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder> {

    private final LayoutInflater layoutInflater;
    String question_ST, A_ST, B_ST, C_ST, D_ST, rightAnswer_ST;
    private List<Question2> questionsList;


    QuestionListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        question_ST = context.getResources().getString(R.string.question_setText);
        A_ST = context.getResources().getString(R.string.a_setText);
        B_ST = context.getResources().getString(R.string.b_setText);
        C_ST = context.getResources().getString(R.string.c_setText);
        D_ST = context.getResources().getString(R.string.d_setText);
        rightAnswer_ST = context.getResources().getString(R.string.rA_setText);
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new QuestionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        if (questionsList != null) {
            Question2 current = questionsList.get(position);
            holder.question.setText(question_ST + " " + current.getTheQuestion());
            holder.A.setText(A_ST + " " + current.getA());
            holder.B.setText(B_ST + " " + current.getB());
            holder.C.setText(C_ST + " " + current.getC());
            holder.D.setText(D_ST + " " + current.getD());
            holder.rightAnswer.setText(rightAnswer_ST + " " + current.getRightAnswer());
        } else {
            holder.question.setText("No question");
            holder.A.setText("No answer");
            holder.B.setText("No answer");
            holder.C.setText("No answer");
            holder.D.setText("No answer");
            holder.rightAnswer.setText("None");
        }
    }

    void setQuestionsList(List<Question2> questions) {
        questionsList = questions;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (questionsList != null)
            return questionsList.size();
        else return 0;
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView question, A, B, C, D, rightAnswer;

        private QuestionViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.questionTextView);
            A = itemView.findViewById(R.id.answer1TextView);
            B = itemView.findViewById(R.id.answer2TextView);
            C = itemView.findViewById(R.id.answer3TextView);
            D = itemView.findViewById(R.id.answer4TextView);
            rightAnswer = itemView.findViewById(R.id.rightAnswerTextView);
        }
    }

}
