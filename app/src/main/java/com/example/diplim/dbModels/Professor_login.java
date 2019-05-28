package com.example.diplim.dbModels;

import java.util.List;

public class Professor_login {
    Integer professor_id;
    String professor_name, professor_login, professor_pass, token;

    public Professor_login(Integer professor_id, String professor_name, String professor_login, String professor_pass, String token) {
        this.professor_id = professor_id;
        this.professor_name = professor_name;
        this.professor_login = professor_login;
        this.professor_pass = professor_pass;
        this.token = token;
    }

    public Professor_login(List<Professor_login> asList) {
    }

    public Integer getProfessor_id() {
        return professor_id;
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

    public String getToken() {
        return token;
    }
}
