package com.apogee.registration.ui.deivcelist

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.apogee.registration.R
import com.apogee.registration.adaptor.BleDeviceAdaptor
import com.apogee.registration.databinding.BluethoothDeviceListLayoutBinding
import com.apogee.registration.datastore.RegistrationAppSharedPref
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.OnItemClickListener
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.displayActionBar
import com.apogee.registration.utils.hide
import com.apogee.registration.utils.safeNavigate
import com.apogee.registration.utils.setUpDialogBox
import com.apogee.registration.utils.show
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
            activity?.setUpDialogBox(
                getString(R.string.login_res),
                getString(R.string.logout_desc),
                "Logout",
                "Cancel",
                success = {
                    lifecycleScope.launch {
                        regSharedPref.logout().also {
                            if (it) {
                                activity?.finish()
                            }
                        }
                    }
                },
                cancelListener = {

                })
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
        binding.swipeRefresh.setOnRefreshListener {
            if (binding.swipeRefresh.isRefreshing) {
                viewModel.startConnection()
            }
        }
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
                    hidePb()
                    var error = (it.data as String?) ?: ""
                    error += (it.exception?.localizedMessage) ?: ""
                    dialog("Failed", error)
                }

                is DataResponse.Loading -> {
                    createLog("BLE_RES", " LOADING ${it.data} ")
                    showPb((it.data as String?) ?: "")
                }

                is DataResponse.Success -> {
                    createLog("BLE_RES", " Success ${it.data} ")
                    hidePb()
                    try {
                        if (it.data is List<*>) {
                            val item = it.data as List<ScanResult>
                            bleAdaptor.notifyDataSetChanged()
                            bleAdaptor.submitList(item)
                        } else if (it.data is String) {
                            dialog("Success", it.data)
                        }
                    } catch (e: Exception) {
                        createLog("LOG_BLE_ADAPTOR", "TESTING  ${e.localizedMessage}")
                        dialog("Failed", e.localizedMessage ?: "Unknown error")
                    }
                }
            }
        }
    }

    private fun dialog(title: String, msg: String) {
        activity?.setUpDialogBox(title, msg, "ok", success = {
            if (title == "Success") {
                val dir =
                    BluetoothDeviceListFragmentDirections.actionDeviceListFragmentToDeviceRegistrationFragment()
                findNavController().safeNavigate(dir)
            }
        }, cancelListener = {

        })
    }


    private fun showPb(txt: String) {
        if (!binding.swipeRefresh.isRefreshing) {
            binding.pbBle.isVisible = true
            binding.msgPb.text = txt
            binding.msgPb.show()
        }
    }

    private fun hidePb() {
        binding.pbBle.isVisible = false
        binding.msgPb.hide()
        binding.swipeRefresh.isRefreshing = false
    }
}