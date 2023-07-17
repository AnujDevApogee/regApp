package com.example.tcp_ble.RetroFit;

import com.example.tcp_ble.model.LoginResponse;
import com.example.tcp_ble.model.LoginRequest;
import com.example.tcp_ble.model.RequestData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiInterface {

    @Headers("Content-Type: application/json")
    @POST("getLoginPersonData")
    Call<LoginResponse> login(@Body LoginRequest body);

    @Headers("Content-Type: application/json")
    @POST("deviceSubscriptionDate")
    Call<String> subscriptionDate(@Body RequestData imei_NoOfDays);
}
