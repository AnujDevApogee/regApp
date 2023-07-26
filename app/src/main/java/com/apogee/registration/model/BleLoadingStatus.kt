package com.apogee.registration.model


sealed class BleLoadingStatus(msg: String){
    class ImeiNumberLoading(msg:String) : BleLoadingStatus(msg)
}