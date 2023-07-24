package com.apogee.registration.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apogee.registration.databinding.DashboardActivtyLayoutBinding
import com.apogee.registration.utils.PermissionUtils
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashBoardActivity : AppCompatActivity() {
    private lateinit var binding: DashboardActivtyLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardActivtyLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}