package com.apogee.registration.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apogee.registration.databinding.ActivityMainBinding
import com.apogee.registration.utils.goToNextActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
       goToNextActivity<LoginActivity>(true)
    }
}