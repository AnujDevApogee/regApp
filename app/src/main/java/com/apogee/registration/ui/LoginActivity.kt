package com.apogee.registration.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.apogee.registration.databinding.LoginActivityLayoutBinding
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.closeKeyboard
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.openKeyBoard
import com.apogee.registration.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityLayoutBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openKeyBoard(binding.userNm)

        viewModel.event.observe(this) {
            it.getContentIfNotHandled()?.let { msg ->
                createLog("Login_res", msg)
            }
        }

        getLoginResponse()

        binding.loginBtn.setOnClickListener {
            val email = binding.userNm.text.toString()
            val pass = binding.passWord.text.toString()
            viewModel.loginUser(email, pass)
        }

    }

    private fun getLoginResponse() {
        viewModel.loginResponse.observe(this) {
            when (it) {
                is DataResponse.Error -> {
                    createLog(
                        "LOGIN_RES",
                        "Error ${it.data} and Exp ${it.exception?.localizedMessage}"
                    )
                }

                is DataResponse.Loading -> {
                    createLog("LOGIN_RES", "Loading ${it.data}")
                }

                is DataResponse.Success -> {
                    createLog("LOGIN_RES", "Success ${it.data}")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        closeKeyboard(binding.passWord)
    }

}