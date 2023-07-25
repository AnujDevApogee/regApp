package com.apogee.registration.model

import android.bluetooth.le.ScanResult

data class BleDeviceConnection(
    val list: List<ScanResult>, val msg: String, val status: String
) {
    companion object {
        enum class BleDeviceStatus {
            AVAILABLE
        }
    }
}
