package com.example.diplim.dbModels;

public class Professor {
    private String professor_login,professor_pass;

    public Professor(String professor_login, String professor_pass) {
        this.professor_login = professor_login;
        this.professor_pass = professor_pass;
    }

    public String getProfessor_login() {
        return professor_login;
    }

    public String getProfessor_pass() {
        return professor_pass;
    }
}
