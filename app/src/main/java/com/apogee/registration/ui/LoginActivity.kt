package com.apogee.registration.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.apogee.registration.databinding.LoginActivityLayoutBinding
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.closeKeyboard
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.goToNextActivity
import com.apogee.registration.utils.invisible
import com.apogee.registration.utils.openKeyBoard
import com.apogee.registration.utils.setUpDialogBox
import com.apogee.registration.utils.show
import com.apogee.registration.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: LoginActivityLayoutBinding

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.event.observe(this) {
            it.getContentIfNotHandled()?.let { msg ->
                createLog("Login_res", msg)
                dialog("Failed",msg)
            }
        }

        viewModel.getLoginResponse()
        getSavedResponse()
        getLoginResponse()
        getModelDataResponse()

        binding.loginBtn.setOnClickListener {
            val username = binding.userNm.text.toString()
            val pass = binding.passWord.text.toString()
            val isRemember = binding.rememberMe.isChecked
            viewModel.loginUser(username, pass, isRemember)
        }

    }

    private fun getSavedResponse() {
        viewModel.saveCredentials.observe(this) {
            binding.userNm.setText(it.first.first)
            binding.passWord.setText(it.first.second)
            binding.rememberMe.isChecked = it.second
        }
    }

    private fun getModelDataResponse() {
        viewModel.moduleResponse.observe(this) {
            when (it) {
                is DataResponse.Error -> {
                    createLog(
                        "LOGIN_RES", "DataBase Error ${it.data} and Exp ${it.exception?.localizedMessage}"
                    )
                    var err = (it.data as String?) ?: ""
                    err += (it.exception?.localizedMessage) ?: ""
                    dialog("Failed", err)
                    hidePb()
                }

                is DataResponse.Loading -> {
                    createLog("LOGIN_RES", "DataBase LOADING ${it.data} ")
                    it.data?.let {
                        showPb()
                    }
                }

                is DataResponse.Success -> {
                    createLog("LOGIN_RES", "DataBase Success ${it.data}")
                    hidePb()
                    goToNextActivity<DashBoardActivity>(true)
                }
            }
        }
    }

    private fun getLoginResponse() {
        viewModel.loginResponse.observe(this) {
            when (it) {
                is DataResponse.Error -> {
                    createLog(
                        "LOGIN_RES", "Error ${it.data} and Exp ${it.exception?.localizedMessage}"
                    )
                    var err = (it.data as String?) ?: ""
                    err += (it.exception?.localizedMessage) ?: ""
                    dialog("Failed", err)
                    hidePb()
                }

                is DataResponse.Loading -> {
                    createLog("LOGIN_RES", " LOADING ${it.data} ")
                    it.data?.let {
                        showPb()
                    }
                }

                is DataResponse.Success -> {
                    createLog("LOGIN_RES", "Success ${it.data}")
                    viewModel.getModuleResponse()
                    /*hidePb()
                    goToNextActivity<DashBoardActivity>(true)*/
                }
            }
        }
    }

    private fun hidePb() {
        binding.loginBtn.show()
        binding.pb.isVisible = false
    }

    private fun showPb() {
        binding.pb.isVisible = true
        binding.loginBtn.invisible()
    }

    private fun dialog(title: String, msg: String) {
        setUpDialogBox(title, msg, "ok", success = {

        }, cancelListener = {

        })
    }

    override fun onResume() {
        super.onResume()
        openKeyBoard(binding.userNm)
    }


    override fun onPause() {
        super.onPause()
        closeKeyboard(binding.passWord)
    }

}