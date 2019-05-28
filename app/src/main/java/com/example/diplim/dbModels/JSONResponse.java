package com.example.diplim.dbModels;

import com.example.diplim.dbModels.Professor_login;

public class JSONResponse {
    private Professor_login account;
    private Professor_login professor;
    private String message;
    private Boolean status;

    public Professor_login getProfessor(){
        return professor;
    }

    public Professor_login getAccount() {
        return account;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getStatus() {
        return status;
    }
}
