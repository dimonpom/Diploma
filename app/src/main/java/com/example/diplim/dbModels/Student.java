package com.example.diplim.dbModels;

public class Student {
    private Integer student_id, group;
    private String student_name,student_login, student_pass, token;

    public Student(Integer student_id, Integer group, String student_name, String student_login, String student_pass, String token) {
        this.student_id = student_id;
        this.group = group;
        this.student_name = student_name;
        this.student_login = student_login;
        this.student_pass = student_pass;
        this.token = token;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public Integer getGroup() {
        return group;
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

    public String getToken() {
        return token;
    }
}
