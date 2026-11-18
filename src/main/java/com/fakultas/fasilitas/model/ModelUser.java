package com.fakultas.fasilitas.model;

public class ModelUser {
    private String id;
    private String username;
    private String role;
    private String status;

    public ModelUser(String id, String username, String role, String status) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}