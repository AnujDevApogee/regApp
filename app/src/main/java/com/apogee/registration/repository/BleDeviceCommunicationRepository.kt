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
import com.apogee.registration.model.BleErrorStatus
import com.apogee.registration.model.BleLoadingStatus
import com.apogee.registration.model.BleSuccessStatus
import com.apogee.registration.user_case.BleProtocolFilter
import com.apogee.registration.user_case.TimeCompare
import com.apogee.registration.utils.BleHelper.BLERENAMESTATUS
import com.apogee.registration.utils.BleHelper.DEVICEREGCONFIRM
import com.apogee.registration.utils.BleHelper.DEVICEREGRECORD
import com.apogee.registration.utils.BleHelper.IEMINUMBER
import com.apogee.registration.utils.BleHelper.valueOf
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.checkVaildString
import com.apogee.registration.utils.createLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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


    private var bleStatus: String? = null

    private var blueRenamingCMD: String? = null

    private var timerStart: Long = -1
    fun setUpConnection() {
        coroutineScope.launch {
            _data.value =
                DataResponse.Loading(BleLoadingStatus.BleSetUpConnectionLoading("Please Wait Setup connection.."))
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
                _data.value =
                    DataResponse.Loading(BleLoadingStatus.BleConnectDeviceLoading("Please Wait connection with device"))
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


    fun sendRequest(byteArray: ByteArray, status: String) {
        coroutineScope.launch {
            try {
                bleStatus = status
                timerStart = System.currentTimeMillis()
                _data.value = when (valueOf(bleStatus!!)) {
                    IEMINUMBER -> {
                        DataResponse.Loading(BleLoadingStatus.ImeiNumberLoading("Please Wait Check For Imei Number"))
                    }

                    DEVICEREGRECORD -> {
                        DataResponse.Loading(BleLoadingStatus.BleDeviceRegRecordLoading("Please Wait For Device Reg"))
                    }

                    DEVICEREGCONFIRM -> {
                        DataResponse.Loading(BleLoadingStatus.BleDeviceConfirmationLoading("Please Wait For Device Reg Confirmation"))
                    }

                    BLERENAMESTATUS -> {
                        blueRenamingCMD = String(byteArray)
                        DataResponse.Loading(BleLoadingStatus.BleRenamingStatusLoading("Please Wait For Renaming Ble Device"))
                    }
                }
                delay(200)
                service!!.write(byteArray)
            } catch (e: Exception) {
                timerStart = -1
                _data.value = if (bleStatus == null) {
                    DataResponse.Error(null, e)
                } else {
                    when (valueOf(bleStatus!!)) {
                        IEMINUMBER -> DataResponse.Error(BleErrorStatus.BleImeiError(null, e), null)
                        DEVICEREGRECORD -> {
                            DataResponse.Error(
                                BleErrorStatus.BleDeviceRegRecordError(null, e),
                                null
                            )
                        }

                        DEVICEREGCONFIRM -> {
                            DataResponse.Error(
                                BleErrorStatus.BleDeviceConfirmationError(null, e),
                                null
                            )
                        }

                        BLERENAMESTATUS -> {
                            DataResponse.Error(
                                BleErrorStatus.BleRenamingStatusError(null, e),
                                null
                            )
                        }
                    }
                }
            }
        }
    }


    override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
        try {
            service = (binder as SerialService.SerialBinder).service
            service!!.attach(this@BleDeviceCommunicationRepository)
            coroutineScope.launch {
                _data.value =
                    DataResponse.Success(BleSuccessStatus.BleSetUpConnectionSuccess("Open for Connection.."))
            }
        } catch (e: Exception) {
            coroutineScope.launch {
                _data.value = DataResponse.Error(
                    BleErrorStatus.BleSetUpConnectionError(
                        "Cannot set the Connection ",
                        e
                    ), null
                )
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
            delay(2000)
            _data.value =
                DataResponse.Success(BleSuccessStatus.BleConnectSuccess("Device is Connected"))//pass cmd
        }
    }

    override fun onSerialConnectError(e: Exception?) {
        coroutineScope.launch {
            createLog("BLE_SERIAL_Error", "Serial Connector ${e?.localizedMessage}")
            val err = if (e == null) "Unknown Error for While Initialing Service" else null
            _data.value = DataResponse.Error(BleErrorStatus.BleConnectError(err, e), null)
        }
    }

    override fun onSerialNmeaRead(p0: String?) {
        createLog("TAG_NMEA", "NMEA STRING -->  $p0")
    }

    override fun onSerialProtocolRead(res: String?) {
        createLog(
            "TAG_PROTOCOL",
            "${if (bleStatus.isNullOrEmpty()) "" else "$bleStatus"} Protocol String $res"
        )
        if (bleStatus != null) {
            coroutineScope.launch {
                when (valueOf(bleStatus!!)) {
                    IEMINUMBER -> {
                        if (timerStart != (-1).toLong()) {
                            if (!TimeCompare.isTimeOut(timerStart, System.currentTimeMillis())) {
                                try {
                                    if (!checkVaildString(res)) {
                                        BleProtocolFilter.getImeiNumber(res!!)?.let {
                                            _data.value = DataResponse.Success(
                                                BleSuccessStatus.BleImeiNumberSuccess(it)
                                            )
                                            timerStart = -1
                                        }
                                    }
                                } catch (e: Exception) {
                                    _data.value = DataResponse.Error(
                                        BleErrorStatus.BleImeiError(
                                            "Re-start the Process Again",
                                            e
                                        ), null
                                    )
                                }

                            } else {
                                timerStart = -1
                                _data.value = DataResponse.Error(
                                    BleErrorStatus.BleImeiError(
                                        "Cannot find the Imei Number",
                                        null
                                    ), null
                                )
                            }
                        }
                    }

                    DEVICEREGRECORD -> {
                        if (timerStart != (-1).toLong()) {
                            if (!TimeCompare.isTimeOut(timerStart, System.currentTimeMillis())) {
                                try {
                                    if (!checkVaildString(res)) {
                                        BleProtocolFilter.getDeviceGeogProtocol(res!!)?.let {
                                            _data.value = DataResponse.Success(
                                                BleSuccessStatus.BleDeviceRegRecordSuccess(it)
                                            )
                                            timerStart = -1
                                        }
                                    }
                                } catch (e: Exception) {
                                    _data.value = DataResponse.Error(
                                        BleErrorStatus.BleDeviceRegRecordError(
                                            "Re-start the Process Again", e
                                        ), null
                                    )
                                }

                            } else {
                                timerStart = -1
                                _data.value = DataResponse.Error(
                                    BleErrorStatus.BleDeviceRegRecordError(
                                        "Cannot find the Device  Subscription Reg Protocol", null
                                    ), null
                                )
                            }
                        }
                    }

                    DEVICEREGCONFIRM -> {
                        if (timerStart != (-1).toLong()) {
                            if (!TimeCompare.isTimeOut(timerStart, System.currentTimeMillis())) {
                                try {
                                    if (!checkVaildString(res)) {
                                        BleProtocolFilter.getDeviceBleRegConfirm(res!!)?.let {
                                            _data.value = if (it.isNotEmpty() && it.isNotBlank()) {
                                                DataResponse.Success(
                                                    BleSuccessStatus.BleDeviceConfirmationSuccess(it)
                                                )
                                            } else {
                                                DataResponse.Error(
                                                    BleErrorStatus.BleDeviceConfirmationError(
                                                        "Failed to verify subscription", null
                                                    ), null
                                                )
                                            }
                                            timerStart = -1
                                        }
                                    }
                                } catch (e: Exception) {
                                    _data.value = DataResponse.Error(
                                        BleErrorStatus.BleDeviceConfirmationError(
                                            "Re-start the Process Again", e
                                        ), null
                                    )
                                }

                            } else {
                                timerStart = -1
                                _data.value = DataResponse.Error(
                                    BleErrorStatus.BleDeviceConfirmationError(
                                        "Cannot find the Device Subscription Confirmation Protocol",
                                        null
                                    ), null
                                )
                            }
                        }
                    }

                    BLERENAMESTATUS -> {
                        createLog("TAG_PROTOCOL", "Successes and rename cmd is $blueRenamingCMD")
                        _data.value = if (blueRenamingCMD != null) {
                            timerStart = -1
                            val cmd=blueRenamingCMD
                            blueRenamingCMD=null
                            DataResponse.Success(
                                BleSuccessStatus.BleRenamingStatusSuccess(cmd.toString())
                            )

                        } else {
                            DataResponse.Error(
                                BleErrorStatus.BleRenamingStatusError(
                                    "Failed to Renaming the CMD",
                                    null
                                ), null
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onSerialResponseRead(p0: ByteArray?) {
        if (p0 != null) {
            createLog("TAG_READ_RESPONSE", String(p0))
        }
    }

    override fun onSerialIoError(e: Exception?) {
        coroutineScope.launch {
            createLog("BLE_SERIAL_Error", " Serial IO ${e?.localizedMessage}")
            val err = if (e == null) "Unknown Error for While Initialing Service" else null
            if (blueRenamingCMD==null && (e?.localizedMessage?.trim())!="gatt status 8")
            _data.value = DataResponse.Error(BleErrorStatus.BleSetUpConnectionError(err, e), null)
        }
    }


    fun disconnectService() {
        context.stopService(Intent(context, SerialService::class.java))
    }

}