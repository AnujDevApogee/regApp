package com.apogee.registration.ui.registion

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.apogee.blemodule.CommunicationLibrary.SerialListener
import com.apogee.registration.R
import com.apogee.registration.databinding.DeviceRegistrationLayoutBinding
import com.apogee.registration.utils.calenderPicker
import com.apogee.registration.utils.displayActionBar
import com.apogee.registration.utils.showToastMsg

class DeviceRegistrationFragment : Fragment(R.layout.device_registration_layout),ServiceConnection,SerialListener {
    private lateinit var binding: DeviceRegistrationLayoutBinding


    private val args: DeviceRegistrationFragmentArgs by navArgs()


    private var isSubEdClicked = false


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
        binding.submitBtn.setOnClickListener {
            //showToastMsg(binding.modelNo.text.toString())
        }
    }

    private fun setupUI() {
        binding.bleNme.setText(args.devicename)
        binding.modelNo.apply {
            setText(resources.getStringArray(R.array.model_ls)[0].toString())
            setAdapter(setAdaptor(R.array.model_ls))
        }
        binding.deviceLs.apply {
            setText(resources.getStringArray(R.array.device_ls)[0].toString())
            setAdapter(setAdaptor(R.array.device_ls))
        }
        binding.manufacturerLs.apply {
            setText(resources.getStringArray(R.array.manufact_ls)[0].toString())
            setAdapter(setAdaptor(R.array.manufact_ls))
        }
        binding.modelNameLs.apply {
            setText(resources.getStringArray(R.array.model_name_ls)[0].toString())
            setAdapter(setAdaptor(R.array.model_name_ls))
        }
        binding.subscriptionDate.setOnClickListener {
            if (!isSubEdClicked) {
                calenderPicker(parentFragmentManager, {
                }, {
                    binding.subscriptionDate.setText(it)
                })
            } else {
                showToastMsg("opening the Calender Picker")
            }
            isSubEdClicked = !isSubEdClicked
        }
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

    private fun setAdaptor(strAdp: Int): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            this.requireContext(),
            android.R.layout.select_dialog_item,
            resources.getStringArray(strAdp)
        )
    }
}