package com.apogee.registration.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.apogee.registration.databinding.ActivityMainBinding
import com.apogee.registration.datastore.RegistrationAppSharedPref
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.goToNextActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        goToNextActivity<LoginActivity>(true)

        RegistrationAppSharedPref.getInstance(this).also {
            createLog("TAG_LOGIN","${it.getLoginResponse()}")
        }

    }
}