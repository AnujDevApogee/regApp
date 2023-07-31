package com.apogee.registration.model.pb


sealed class BlePbSuccess(var data: Any) {
    class ConnectionAndImeiSuccess(data: Any) : BlePbSuccess(data)
    class DeviceRegAndSubSuccess(data: Any) : BlePbSuccess(data)
    class DeviceRegBleAndDeviceConfirmSuccess(data: Any) : BlePbSuccess(data)
    class BLESubBLEAndDeviceSubBleApiSuccess(data: Any) : BlePbSuccess(data)
    class BLERenamingAndApISuccess(data: Any) : BlePbSuccess(data)
    class ValidateUserDetailSuccess(data: Any) : BlePbSuccess(data)
}