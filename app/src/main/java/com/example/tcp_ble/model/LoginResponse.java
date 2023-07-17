package com.example.tcp_ble.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginResponse {
    @SerializedName("Data")
    @Expose
     List<LoginDetails> data ;

    public List<LoginDetails> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "data=" + data +
                '}';
    }
}
