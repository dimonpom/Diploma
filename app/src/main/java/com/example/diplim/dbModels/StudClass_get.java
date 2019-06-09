package com.example.diplim.dbModels;

public class StudClass_get {
    String student_name,group_name;

    public StudClass_get(String student_name, String group_name) {
        this.student_name = student_name;
        this.group_name = group_name;
    }

    public String getStudent_name() {
        return student_name;
    }

    public String getGroup_name() {
        return group_name;
    }
}
