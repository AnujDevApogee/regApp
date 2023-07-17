package com.example.tcp_ble.model;
import com.google.gson.annotations.SerializedName;







public class LoginRequest {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("project_name")
    private String project_name;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProject_name() {
        return project_name;
    }


    // Add getters and setters for other fields as needed
}


