package com.example.demo.entity.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public RegisterRequest() {}

    public RegisterRequest(String username, String password
        , String email
    ) {
        this.username = username;
        this.password = password;
        this.email = email;
    }


}
