package com.apogee.registration.model

sealed class BleSuccessStatus(var data:Any) {
    class BleImeiNumber(data: Any):BleSuccessStatus(data)
    class BleSetUpConnectionSuccess(data: Any):BleSuccessStatus(data)
    class BleConnectSuccess(data: Any):BleSuccessStatus(data)
}