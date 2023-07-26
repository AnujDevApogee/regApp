package com.apogee.registration.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apogee.registration.databinding.ActivityMainBinding
import com.apogee.registration.datastore.RegistrationAppSharedPref
import com.apogee.registration.utils.PermissionUtils
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.goToNextActivity
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch {
            delay(2000)
            getToPermission()
        }

    }

    private fun getToPermission() {
        PermissionX.init(this@SplashScreenActivity).permissions(PermissionUtils.permissions)
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK",
                    "Cancel"
                )
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to allow necessary permissions in Settings manually",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    goToNextActivity<DashBoardActivity>(true)
                    /*RegistrationAppSharedPref.getInstance(this@SplashScreenActivity).also {
                        createLog("TAG_LOGIN", "${it.getLoginResponse()}")
                        if (it.getLoginResponse() == null || it.getLoginResponse()!!.data.isNullOrEmpty()) {
                            goToNextActivity<LoginActivity>(true)
                        } else {
                            goToNextActivity<DashBoardActivity>(true)
                        }
                    }*/
                } else {
                    getToPermission()
                }
            }
    }
}