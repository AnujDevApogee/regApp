package com.apogee.registration.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.apogee.blemodule.CommunicationLibrary.SerialListener
import com.apogee.blemodule.CommunicationLibrary.SerialService
import com.apogee.blemodule.CommunicationLibrary.SerialSocket
import com.apogee.registration.instance.BluetoothCommunication
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.createLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BleDeviceCommunicationRepository(
    bleAdaptor: BluetoothCommunication, private val context: Context
) : ServiceConnection, SerialListener {


    private var service: SerialService? = null


    private val bluetoothAdaptor by lazy {
        bleAdaptor.getBluetoothAdaptor()
    }


    private val _data = MutableStateFlow<DataResponse<out Any>?>(null)
    val data: MutableStateFlow<DataResponse<out Any>?>
        get() = _data


    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    fun setUpConnection() {
        coroutineScope.launch {
            _data.value = DataResponse.Loading("Please Wait Setup..")
            context.bindService(
                Intent(context, SerialService::class.java),
                this@BleDeviceCommunicationRepository,
                AppCompatActivity.BIND_AUTO_CREATE
            )
        }
    }

    fun connect(macAddress: String) {
        coroutineScope.launch {
            try {
                val device = bluetoothAdaptor.getRemoteDevice(macAddress)
                val socket = SerialSocket(context, device)
                service!!.connect(socket)
                _data.value=DataResponse.Loading("Please Wait connection with device")
            } catch (e: Exception) {
                onSerialConnectError(e)
            }
        }
    }


    fun disconnect() {
        coroutineScope.launch {
            try {
                service!!.disconnect()
                _data.value = DataResponse.Success("Disconnect with Device")
            } catch (e: Exception) {
                _data.value = DataResponse.Error(null, e)
            }
        }
    }


    fun sendRequest(byteArray: ByteArray) {
        coroutineScope.launch {
            try {
                service!!.write(byteArray)
                _data.value = DataResponse.Loading("Sending Request")
            }catch (e:Exception){
                _data.value=DataResponse.Error(null,e)
            }
        }
    }


    override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
        try {
            service = (binder as SerialService.SerialBinder).service
            service!!.attach(this@BleDeviceCommunicationRepository)
            coroutineScope.launch {
                _data.value = DataResponse.Success("Open for Connection..")
            }
        } catch (e: Exception) {
            coroutineScope.launch{
                _data.value = DataResponse.Error("Cannot set the Connection ", e)
            }
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        coroutineScope.launch {
            _data.value = DataResponse.Success("Disconnected with Service")
        }
    }

    override fun onSerialConnect() {
        createLog("TAG_CONNECT", "Connected")
        coroutineScope.launch {
            _data.value = DataResponse.Success("Device is Connected")
        }
    }

    override fun onSerialConnectError(e: Exception?) {
        coroutineScope.launch {
            createLog("BLE_SERIAL_Error","Serial Connector ${e?.localizedMessage}")
            val err = if (e == null) "Unknown Error for While Initialing Service" else null
            _data.value = DataResponse.Error(err, e)
        }
    }

    override fun onSerialNmeaRead(p0: String?) {
        createLog("TAG_NMEA", "NMEA STRING -->  $p0")
    }

    override fun onSerialProtocolRead(p0: String?) {
        createLog("TAG_PROTOCOL", "Protocol String $p0")
    }

    override fun onSerialResponseRead(p0: ByteArray?) {
        if (p0 != null) {
            createLog("TAG_READ_RESPONSE", String(p0))
        }
    }

    override fun onSerialIoError(e: Exception?) {
        coroutineScope.launch {
            createLog("BLE_SERIAL_Error"," Serial IO ${e?.localizedMessage}")
            val err = if (e == null) "Unknown Error for While Initialing Service" else null
            _data.value = DataResponse.Error(err, e)
        }
    }


    fun disconnectService() {
        context.stopService(Intent(context, SerialService::class.java))
    }

}