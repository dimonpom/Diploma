package com.example.diplim.dbModels;

public class Professor_register {
    String professor_name,professor_login,professor_pass;

    public Professor_register(String professor_name, String professor_login, String professor_pass) {
        this.professor_name = professor_name;
        this.professor_login = professor_login;
        this.professor_pass = professor_pass;
    }

    public String getProfessor_name() {
        return professor_name;
    }

    public String getProfessor_login() {
        return professor_login;
    }

    public String getProfessor_pass() {
        return professor_pass;
    }
}
