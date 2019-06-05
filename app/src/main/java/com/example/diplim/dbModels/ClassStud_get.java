package com.example.diplim.dbModels;

public class ClassStud_get {
    int class_id;
    String class_date, subject, professor, theme, groups;

    public ClassStud_get(int class_id, String class_date, String subject, String professor, String theme, String groups) {
        this.class_id = class_id;
        this.class_date = class_date;
        this.subject = subject;
        this.professor = professor;
        this.theme = theme;
        this.groups = groups;
    }

    public int getClass_id() {
        return class_id;
    }

    public String getClass_date() {
        return class_date;
    }

    public String getSubject() {
        return subject;
    }

    public String getProfessor() {
        return professor;
    }

    public String getTheme() {
        return theme;
    }

    public String getGroups() {
        return groups;
    }
}
