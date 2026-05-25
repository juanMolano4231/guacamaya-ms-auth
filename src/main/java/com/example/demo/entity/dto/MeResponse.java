package com.example.demo.entity.dto;

public class MeResponse {

    private Long id;
    private String username;
    private String role;

    public MeResponse() {}

    public MeResponse(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() { return id; }

    public String getUsername() { return username; }

    public String getRole() { return role; }
}