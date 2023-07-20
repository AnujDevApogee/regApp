package com.apogee.registration

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apogee.registration.databinding.LoginActivityLayoutBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityLayoutBinding.inflate(layoutInflater)

    }

}