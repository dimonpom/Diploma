package com.example.diplim.dbModels;

public class Presence_post {
    int student_id,class_id;

    public Presence_post(int student_id, int class_id) {
        this.student_id = student_id;
        this.class_id = class_id;
    }

    public int getStudent_id() {
        return student_id;
    }

    public int getClass_id() {
        return class_id;
    }
}
