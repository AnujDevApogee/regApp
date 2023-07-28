package com.apogee.registration.model


sealed class BleLoadingStatus(val msg: String){
    class BleSetUpConnectionLoading(msg: String) : BleLoadingStatus(msg)
    class BleConnectDeviceLoading(msg: String) : BleLoadingStatus(msg)
    class BleDeviceRegRecordLoading(msg: String) : BleLoadingStatus(msg)
    class BleDeviceConfirmationLoading(msg: String) : BleLoadingStatus(msg)
    class ImeiNumberLoading(msg: String) : BleLoadingStatus(msg)
}