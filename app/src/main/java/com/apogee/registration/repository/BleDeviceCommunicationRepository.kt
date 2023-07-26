package com.apogee.registration.repository

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.apogee.blemodule.CommunicationLibrary.SerialListener
import com.apogee.blemodule.CommunicationLibrary.SerialService
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.createLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BleDeviceCommunicationRepository(
    private val bleAdaptor: BluetoothAdapter,
    private val context: Context
) : ServiceConnection, SerialListener {

    private val _data = MutableStateFlow<DataResponse<out Any>?>(null)
    val data: MutableStateFlow<DataResponse<out Any>?>
        get() = _data


    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    fun setUpConnection() {
        context.bindService(
            Intent(context, SerialService::class.java), this,
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }

    fun connect(macAddress: String) {
        coroutineScope.launch {

        }
    }


    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        TODO("Not yet implemented")
    }

    override fun onSerialConnect() {
        createLog("TAG_CONNECT","Connected")
    }

    override fun onSerialConnectError(p0: Exception?) {
        TODO("Not yet implemented")
    }

    override fun onSerialNmeaRead(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onSerialProtocolRead(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onSerialResponseRead(p0: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun onSerialIoError(p0: Exception?) {
        TODO("Not yet implemented")
    }


}