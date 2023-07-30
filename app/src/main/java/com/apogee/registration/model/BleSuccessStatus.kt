package com.apogee.registration.model

sealed class BleSuccessStatus(var data: Any) {
    class BleImeiNumberSuccess(data: Any) : BleSuccessStatus(data)
    class BleSetUpConnectionSuccess(data: Any) : BleSuccessStatus(data)
    class BleConnectSuccess(data: Any) : BleSuccessStatus(data)
    class BleDeviceRegRecordSuccess(data: Any) : BleSuccessStatus(data)
    class BleDeviceConfirmationSuccess(data: Any) : BleSuccessStatus(data)
    class BleRenamingStatusSuccess(data: Any) : BleSuccessStatus(data)
}