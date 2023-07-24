package com.apogee.registration.ui.registion

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.apogee.registration.R
import com.apogee.registration.databinding.DeviceRegistrationLayoutBinding
import com.apogee.registration.utils.displayActionBar

class DeviceRegistrationFragment : Fragment(R.layout.device_registration_layout) {
    private lateinit var binding: DeviceRegistrationLayoutBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DeviceRegistrationLayoutBinding.bind(view)
        displayActionBar(
            getString(R.string.ble_reg),
            binding.actionLayout,
            bckIc = R.drawable.ic_back_arrow
        )
    }
}