package com.apogee.registration.ui.registion

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.apogee.blemodule.CommunicationLibrary.SerialListener
import com.apogee.registration.R
import com.apogee.registration.databinding.DeviceRegistrationLayoutBinding
import com.apogee.registration.utils.displayActionBar

class DeviceRegistrationFragment : Fragment(R.layout.device_registration_layout),ServiceConnection,SerialListener {
    private lateinit var binding: DeviceRegistrationLayoutBinding


    private val args: DeviceRegistrationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DeviceRegistrationLayoutBinding.bind(view)
        displayActionBar(
            getString(R.string.ble_reg),
            binding.actionLayout,
            bckIc = R.drawable.ic_back_arrow
        )
        setupUI()
        /* activity?.bindService(Intent(requireActivity(), SerialService::class.java), this,
             AppCompatActivity.BIND_AUTO_CREATE
         )*/

    }

    private fun setupUI() {
        binding.bleNme.setText(args.devicename)
    }

    override fun onStart() {
        super.onStart()

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