package com.example.recipegeneratorapplication;
import com.google.gson.annotations.SerializedName;

public class LoginResult {

    @SerializedName("username")
    private String username;
    @SerializedName("email")

    private String email;
    @SerializedName("password")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }



}
