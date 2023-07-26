package com.apogee.registration.repository

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.apogee.blemodule.CommunicationLibrary.SerialListener
import java.lang.Exception

class BleDeviceCommunicationRepository(private val bleAdaptor:BluetoothAdapter): ServiceConnection, SerialListener {








    fun connect(macAddress:String){

    }


    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

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