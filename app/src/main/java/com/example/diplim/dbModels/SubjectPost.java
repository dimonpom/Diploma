package com.example.diplim.dbModels;

public class SubjectPost {

    private Integer subject_id;
    private String subject_name;

    public SubjectPost(Integer subject_id, String subject_name) {
        this.subject_id = subject_id;
        this.subject_name = subject_name;
    }

    public int getSubject_id() {
        return subject_id;
    }

    public String getSubject_name() {
        return subject_name;
    }
}
