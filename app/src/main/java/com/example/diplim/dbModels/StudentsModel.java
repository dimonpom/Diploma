package com.example.diplim.dbModels;

public class StudentsModel {
    String fullName;
    String fullGroup;

    public StudentsModel(String fullName, String fullGroup) {
        this.fullName = fullName;
        this.fullGroup = fullGroup;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFullGroup() {
        return fullGroup;
    }
}
