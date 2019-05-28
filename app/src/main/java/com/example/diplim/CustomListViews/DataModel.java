package com.example.diplim.CustomListViews;

public class DataModel {
    int id;
    String subject, theme, date;

    public DataModel(int id, String subject, String theme, String date) {
        this.id = id;
        this.subject = subject;
        this.theme = theme;
        this.date = date;
    }

    public int getId(){
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getTheme() {
        return theme;
    }

    public String getDate() {
        return date;
    }
}
