package com.example.tcp_ble.RetroFit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroFitClient {
    private static final String BASE_URL="http://120.138.10.146:8080/login_module/api/";
    private static final String BASE_URL2="http://192.168.1.11:8082/RegistrationCumAllotment/";
    private static Retrofit retrofit=null;

    public static ApiInterface getRetofitClient(int RequestCode){
        if (retrofit==null){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            if(RequestCode==10){
                retrofit=new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
            else if(RequestCode==20){
                retrofit=new Retrofit.Builder()
                        .baseUrl(BASE_URL2).client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }

        }
        return retrofit.create(ApiInterface.class);
    };
}
