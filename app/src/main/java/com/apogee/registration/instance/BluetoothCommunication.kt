package com.apogee.registration.instance

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

class BluetoothCommunication(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: BluetoothCommunication? = null

        fun getInstance(applicationContext: Context): BluetoothCommunication {
            if (INSTANCE == null) {
                INSTANCE = BluetoothCommunication(applicationContext)
            }
            return INSTANCE!!
        }

    }


    fun getBluetoothAdaptor(): BluetoothAdapter {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

}