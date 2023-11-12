package com.community.weare.Models.dto;

public class UserDtoRequest {

    int id;
    String username;


    public UserDtoRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
