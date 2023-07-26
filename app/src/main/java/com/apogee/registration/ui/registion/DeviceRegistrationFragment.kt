package com.apogee.registration.ui.registion

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.apogee.blemodule.CommunicationLibrary.SerialListener
import com.apogee.blemodule.CommunicationLibrary.SerialService
import com.apogee.blemodule.CommunicationLibrary.SerialSocket
import com.apogee.registration.R
import com.apogee.registration.databinding.DeviceRegistrationLayoutBinding
import com.apogee.registration.instance.BluetoothCommunication
import com.apogee.registration.ui.registion.DeviceRegistrationFragment.Companion.Communcation.Connect
import com.apogee.registration.ui.registion.DeviceRegistrationFragment.Companion.Communcation.Disconnect
import com.apogee.registration.ui.registion.DeviceRegistrationFragment.Companion.Communcation.Write
import com.apogee.registration.ui.registion.DeviceRegistrationFragment.Companion.Communcation.valueOf
import com.apogee.registration.utils.calenderPicker
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.displayActionBar
import com.apogee.registration.utils.newline_crlf
import com.apogee.registration.utils.showToastMsg

class DeviceRegistrationFragment : Fragment(R.layout.device_registration_layout),ServiceConnection,SerialListener {

    private lateinit var binding: DeviceRegistrationLayoutBinding

    private val args: DeviceRegistrationFragmentArgs by navArgs()

    private var isSubEdClicked = false

    private var service: SerialService? = null

    private var status = Connect.name

    companion object {
        enum class Communcation {
            Connect,
            Disconnect,
            Write
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DeviceRegistrationLayoutBinding.bind(view)
        displayActionBar(
            getString(R.string.ble_reg),
            binding.actionLayout,
            bckIc = R.drawable.ic_back_arrow
        )
        setupUI()
        activity?.bindService(
            Intent(requireActivity(), SerialService::class.java), this,
            AppCompatActivity.BIND_AUTO_CREATE
        )
        binding.submitBtn.setOnClickListener {
            //showToastMsg(binding.modelNo.text.toString())
            when (valueOf(status)) {
                Connect -> {
                    try {
                        val bluetoothAdapter =
                            BluetoothCommunication(requireContext()).getBluetoothAdaptor()
                        val device = bluetoothAdapter.getRemoteDevice(args.macaddress)
                        // connected = Connected.Pending
                        val socket = SerialSocket(activity?.applicationContext, device)
                        service!!.connect(socket)
                    } catch (e: java.lang.Exception) {
                        onSerialConnectError(e)
                    }
                }

                Disconnect -> {
                    service?.disconnect()
                }

                Write -> {

                  /*  val s = "b562068a090000010000bc0091200f16df"
                    val ans = ByteArray(s.length / 2)

                    println("Hex String : $s")

                    for (i in ans.indices) {
                        val index = i * 2

                        // Using parseInt() method of Integer class
                        val `val` = s.substring(index, index + 2).toInt(16)
                        ans[i] = `val`.toByte()
                    }
*/
                  var imeiQuery ="$$$$,03,03,3,1,0,0000,####"
                    imeiQuery += newline_crlf
                    service?.write(imeiQuery.toByteArray())
                }
            }
            /*   try {
                   val bleAdaptor=BluetoothAdapter.getDefaultAdapter()//BluetoothCommunication(requireContext()).getBluetoothAdaptor()
                   val device=bleAdaptor.getRemoteDevice(args.macaddress)
                   val service=SerialSocket(requireContext(),device)
                   this.service?.connect(service)
               }catch (e:Exception){
                   this.service?.onSerialConnectError(e)
               }*/
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
        service?.attach(this) ?: activity?.startService(
            Intent(
                requireActivity(),
                SerialService::class.java
            )
        )
    }

    override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
        showToastMsg("connection with service")
        service = (binder as SerialService.SerialBinder).service
        service?.attach(this)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        showToastMsg("Disconnected")
    }

    override fun onSerialConnect() {
        status = Write.name
        showToastMsg("Connected")
    }

    override fun onSerialConnectError(p0: Exception?) {
        showToastMsg("Error While Connection")
        createLog("TAG_ble"," connection Error ${p0?.localizedMessage}")
    }

    override fun onSerialRead(p0: ByteArray?) {
      p0?.let {
          val string= String(p0)
          createLog("TAG_ble","Response -> $string")
      }
    }

    override fun onSerialIoError(p0: Exception?) {
        createLog("TAG_ble","Error -> ${p0?.localizedMessage}")
    }

    private fun setAdaptor(strAdp: Int): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            this.requireContext(),
            android.R.layout.select_dialog_item,
            resources.getStringArray(strAdp)
        )
    }
}