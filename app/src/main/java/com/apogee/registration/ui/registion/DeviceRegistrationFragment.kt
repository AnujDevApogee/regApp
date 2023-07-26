package com.apogee.registration.ui.registion

import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.fragment.app.Fragment
import com.apogee.blemodule.CommunicationLibrary.SerialListener
import com.apogee.registration.R
import com.apogee.registration.databinding.DeviceRegistrationLayoutBinding
import com.apogee.registration.utils.displayActionBar
import java.lang.Exception

class DeviceRegistrationFragment : Fragment(R.layout.device_registration_layout),ServiceConnection,SerialListener {
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

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        TODO("Not yet implemented")
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        TODO("Not yet implemented")
    }

    override fun onSerialConnect() {
        TODO("Not yet implemented")
    }

    override fun onSerialConnectError(p0: Exception?) {
        TODO("Not yet implemented")
    }

    override fun onSerialRead(p0: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun onSerialIoError(p0: Exception?) {
        TODO("Not yet implemented")
    }

}