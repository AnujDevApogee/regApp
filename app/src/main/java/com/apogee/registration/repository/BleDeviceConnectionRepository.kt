package com.apogee.registration.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.apogee.registration.utils.DataResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class BleDeviceConnectionRepository(
    private val bluetoothAdapter: BluetoothAdapter, private val context: Context
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
                    _bleConnection.value = DataResponse.Success(deviceList)
                }
            }
            /*   if (result?.device?.address == DEVICE_NAME) {
                   coroutineScope.launch {
                       _data.value =
                           (Response.Loading(message = "Connecting with ${result.device.name}"))
                   }
                   if (isScanning) {
                       result.device.connectGatt(
                           context, false, gattCallback, BluetoothDevice.TRANSPORT_LE
                       )*//*, BluetoothDevice.TRANSPORT_LE
                    )*//*
                    //BluetoothDevice.TRANSPORT_LE only if when you use normal device
                    isScanning = false
                    bleScanner.stopScan(this)
                }
            }*/
        }
    }

    suspend fun startConnection() {
        _bleConnection.value = (DataResponse.Loading("Scanning Ble devices..."))
        bleScanner.startScan(null, scanSetting.build(), scanCallback)
    }


}