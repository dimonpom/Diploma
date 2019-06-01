package com.example.diplim.dbModels;

public class Question_answer {
    int question_id;
    String question_text;

    public int getQuestion_id() {
        return question_id;
    }

    public String getQuestion_text() {
        return question_text;
    }

    public Question_answer(int question_id, String question_text) {
        this.question_id = question_id;
        this.question_text = question_text;
    }
}
