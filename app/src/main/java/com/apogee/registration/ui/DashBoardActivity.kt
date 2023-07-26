package com.apogee.registration.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.apogee.registration.databinding.DashboardActivtyLayoutBinding
import com.apogee.registration.instance.BluetoothCommunication
import com.apogee.registration.utils.BlueToothBroadCastReceiver
import com.apogee.registration.utils.createLog

class DashBoardActivity : AppCompatActivity(){
    private lateinit var binding: DashboardActivtyLayoutBinding

    private val bluTohAdaptor by lazy {
        BluetoothCommunication.getInstance(this).getBluetoothAdaptor()
    }
    private var bluetoothReg: BlueToothBroadCastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardActivtyLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bluetoothReg = BlueToothBroadCastReceiver(this) {
            if (it.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                createLog("BLE_SC", "CONNECTION CHANGE")
                showBluetoothEnableDialog()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        showBluetoothEnableDialog()

    }

    private fun showBluetoothEnableDialog() {
        if (!bluTohAdaptor.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerActivity.launch(intent)
        }
    }


    private val registerActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                showBluetoothEnableDialog()
            }
        }

    override fun onResume() {
        super.onResume()
        bluetoothReg?.registerReceiver(BluetoothAdapter.ACTION_STATE_CHANGED)
    }

    override fun onPause() {
        super.onPause()
        bluetoothReg?.unregisterReceiver()
    }

}