package com.apogee.registration.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apogee.registration.databinding.DashboardActivtyLayoutBinding

class DashBoardActivity : AppCompatActivity() {
    private lateinit var binding: DashboardActivtyLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardActivtyLayoutBinding.inflate(layoutInflater)

    }

}