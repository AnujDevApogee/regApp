package com.apogee.registration.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import com.apogee.registration.model.BleDeviceConnection
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.getEmojiByUnicode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class BleDeviceConnectionRepository(
    private val bluetoothAdapter: BluetoothAdapter
) {


    private val _bleConnection =
        MutableStateFlow<DataResponse<out Any?>>(DataResponse.Loading(null))
    val bleConnection: StateFlow<DataResponse<out Any?>>
        get() = _bleConnection


    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSetting = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)

    private val deviceList = mutableListOf<ScanResult>()


    private val scanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            coroutineScope.launch {
                try {
                    if (result != null && result.device?.name != null && result.device.address != null) {
                        Log.i(
                            "deviceNm",
                            "onScanResult: ${result.device.name}  ${result.device.address}"
                        )
                        val res = deviceList.find {
                            it.device.address == result.device.address
                        }
                        if (res == null) {
                            deviceList.add(result)
                        }
                        //_bleConnection.value = DataResponse.Success(deviceList)
                        //delay(1000)
                    }
                } catch (e: Exception) {
                    createLog("TAG_INFO", e.localizedMessage?:"Unknown error")
                }
            }
        }
    }


    suspend fun startConnection() {
        try {
            _bleConnection.value =
                (DataResponse.Loading("Scanning Ble devices... ${getEmojiByUnicode(0x1F50E)}"))
            bleScanner.startScan(null, scanSetting.build(), scanCallback)
            delay(5000)
            _bleConnection.value = (DataResponse.Success(
                BleDeviceConnection(
                    deviceList,
                    "",
                    BleDeviceConnection.Companion.BleDeviceStatus.AVAILABLE.name
                )
            ))
            bleScanner.stopScan(scanCallback)
        } catch (e: Exception) {
            _bleConnection.value = (DataResponse.Error(null, e))
        }
    }


}