package com.apogee.registration.ui.deivcelist

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.apogee.registration.R
import com.apogee.registration.adaptor.BleDeviceAdaptor
import com.apogee.registration.databinding.BluethoothDeviceListLayoutBinding
import com.apogee.registration.datastore.RegistrationAppSharedPref
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.OnItemClickListener
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.displayActionBar
import com.apogee.registration.viewmodel.BleConnectionViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

class BluetoothDeviceListFragment : Fragment(R.layout.bluethooth_device_list_layout) {
    private lateinit var binding: BluethoothDeviceListLayoutBinding

    private val regSharedPref by lazy {
        RegistrationAppSharedPref.getInstance(requireContext())
    }

    private val viewModel: BleConnectionViewModel by viewModels()

    private lateinit var bleAdaptor: BleDeviceAdaptor


    private val mnuCallBack = object :
        OnItemClickListener {
        override fun <T> onClickListener(response: T) {
            lifecycleScope.launch {
                regSharedPref.logout().also {
                    if (it) {
                        activity?.finish()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = BluethoothDeviceListLayoutBinding.bind(view)
        displayActionBar(
            getString(R.string.navik_reg),
            binding.actionLayout,
            R.menu.info_mnu,
            onMenuItem = mnuCallBack
        )
        setupRecycleAdaptor()
        getBleDevice()
    }

    private fun setupRecycleAdaptor() {
        binding.recycleViewBle.apply {
            bleAdaptor = BleDeviceAdaptor {
                createLog("BLE_CLICK", "$it")
                viewModel.connectDevice(it)
            }
            adapter = bleAdaptor
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Suppress("UNCHECKED_CAST")
    private fun getBleDevice() {
        viewModel.bleDeviceAvailable.observe(viewLifecycleOwner) {
            when (it) {
                is DataResponse.Error -> {
                    createLog(
                        "BLE_RES", "Error ${it.data} and Exp ${it.exception?.localizedMessage}"
                    )
                    Toast.makeText(activity, "${it.data}", Toast.LENGTH_SHORT).show()
                    hidePb()
                }

                is DataResponse.Loading -> {
                    createLog("BLE_RES", " LOADING ${it.data} ")
                    showPb()
                }

                is DataResponse.Success -> {
                    createLog("BLE_RES", " Success ${it.data} ")
                    hidePb()
                   try {
                       val item = it.data as List<ScanResult>
                       bleAdaptor.notifyDataSetChanged()
                       bleAdaptor.submitList(item)
                   }catch (e:Exception){
                       createLog("LOG","TESTING")
                   }
                }
            }
        }
    }

    private fun showPb() {
        binding.pbBle.isVisible=true
    }
    private fun hidePb(){
        binding.pbBle.isVisible=false
    }
}