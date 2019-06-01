package com.example.diplim.dbModels;

public class Answer_answer {
    int answer_id, question_id, student_id;
    String answer_text;

    public Answer_answer(int answer_id, int question_id, int student_id, String answer_text) {
        this.answer_id = answer_id;
        this.question_id = question_id;
        this.student_id = student_id;
        this.answer_text = answer_text;
    }

    public int getAnswer_id() {
        return answer_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public String getAnswer_text() {
        return answer_text;
    }
}
