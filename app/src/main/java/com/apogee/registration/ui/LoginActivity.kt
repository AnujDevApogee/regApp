package com.apogee.registration.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apogee.registration.databinding.LoginActivityLayoutBinding
import com.apogee.registration.utils.closeKeyboard
import com.apogee.registration.utils.goToNextActivity
import com.apogee.registration.utils.openKeyBoard

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openKeyBoard(binding.userNm)
        binding.loginBtn.setOnClickListener {
            goToNextActivity<DashBoardActivity>(true)
        }
    }

    override fun onPause() {
        super.onPause()
        closeKeyboard(binding.passWord)
    }

}