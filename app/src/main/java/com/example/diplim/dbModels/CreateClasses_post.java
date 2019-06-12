package com.example.diplim.dbModels;

public class CreateClasses_post {
    String class_date;
    int subject,professor;
    String theme;

    public CreateClasses_post(String class_date, int subject, int professor, String theme) {
        this.class_date = class_date;
        this.subject = subject;
        this.professor = professor;
        this.theme = theme;
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
