package com.example.diplim.dbModels;

public class DataModel_stud {
    int id;
    String subject, profName, date;

    public DataModel_stud(int id, String subject, String profName, String date) {
        this.id = id;
        this.subject = subject;
        this.profName = profName;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getProfName() {
        return profName;
    }

    public String getDate() {
        return date;
    }
}
