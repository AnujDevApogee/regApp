package com.example.tcp_ble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tcp_ble.RetroFit.RetroFitClient;
import com.example.tcp_ble.databinding.ActivityLoginBinding;
import com.example.tcp_ble.model.LoginResponse;
import com.example.tcp_ble.model.LoginRequest;
import com.example.tcp_ble.utility.Constants;
import com.example.tcp_ble.utility.DeviceScanActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        binding.cardLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchPosts();
            }
        });

    }


    private void fetchPosts() {
        LoginRequest request = new LoginRequest();
        request.setUsername(binding.etUsername.getText().toString().trim());
        request.setPassword(binding.etPassword.getText().toString().trim());
        request.setProject_name("RegistrationCumAllotment");

        Call<LoginResponse> call = RetroFitClient.getRetofitClient(10).login(request);
        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {

                    LoginResponse loginResponse = (LoginResponse) response.body();
                    if (loginResponse != null) {
                        Log.d("TAG", "onResponse: " + loginResponse.getData());
                        if (loginResponse.getData().size() > 0) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.NAME, loginResponse.getData().get(0).getName());
                            editor.putString(Constants.KEY_PERSON_ID, loginResponse.getData().get(0).getKeyPersonId());
                            editor.apply();
                            Intent i = new Intent(LoginActivity.this, DeviceScanActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Failed To Validate! Please try later", Toast.LENGTH_SHORT).show();

            }
        });
    }
}