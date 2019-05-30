package com.example.diplim.dbModels;

public class Student_register {
    String student_name, student_login, student_pass;
    Integer group;

    public Student_register(String student_name, String student_login, String student_pass, Integer group) {
        this.student_name = student_name;
        this.student_login = student_login;
        this.student_pass = student_pass;
        this.group = group;
    }

    public String getStudent_name() {
        return student_name;
    }

    public String getStudent_login() {
        return student_login;
    }

    public String getStudent_pass() {
        return student_pass;
    }

    public Integer getGroup() {
        return group;
    }
}