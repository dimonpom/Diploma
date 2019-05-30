package com.example.diplim.dbModels;

public class Student_login {
    String student_login,student_pass;

    public Student_login(String student_login, String student_pass) {
        this.student_login = student_login;
        this.student_pass = student_pass;
    }

    public String getStudent_login() {
        return student_login;
    }

    public String getStudent_pass() {
        return student_pass;
    }
}
