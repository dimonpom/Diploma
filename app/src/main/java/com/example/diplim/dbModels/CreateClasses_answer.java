package com.example.diplim.dbModels;

public class CreateClasses_answer {
    int class_id;
    String class_date;
    int subject, professor;
    String theme;

    public CreateClasses_answer(int class_id, String class_date, int subject, int professor, String theme) {
        this.class_id = class_id;
        this.class_date = class_date;
        this.subject = subject;
        this.professor = professor;
        this.theme = theme;
    }

    public int getClass_id() {
        return class_id;
    }

    public String getClass_date() {
        return class_date;
    }

    public int getSubject() {
        return subject;
    }

    public int getProfessor() {
        return professor;
    }

    public String getTheme() {
        return theme;
    }
}
