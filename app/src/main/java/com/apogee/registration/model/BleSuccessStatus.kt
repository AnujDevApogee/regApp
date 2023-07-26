package com.apogee.registration.model

sealed class BleSuccessStatus(var data:Any) {
    class BleImeiNumber(data: Any):BleSuccessStatus(data)
}