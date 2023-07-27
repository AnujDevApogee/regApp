package com.apogee.registration.model


sealed class BleLoadingStatus(val msg: String){
    class BleSetUpConnection(msg: String):BleLoadingStatus(msg)
    class BleConnectDevice(msg: String):BleLoadingStatus(msg)
    class ImeiNumberLoading(msg:String) : BleLoadingStatus(msg)
}