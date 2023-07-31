package com.apogee.registration.model.pb

sealed class BlePbLoading(val msg: String) {
    class ConnectionAndImeiLoading(msg: String) : BlePbLoading(msg)
    class DeviceRegAndSubLoading(msg: String) : BlePbLoading(msg)
    class DeviceRegBleAndDeviceConfirmLoading(msg: String) : BlePbLoading(msg)
    class BLESubBLEAndDeviceSubBleApiLoading(msg: String) : BlePbLoading(msg)
    class BLERenamingAndApILoading(msg: String) : BlePbLoading(msg)
    class ValidateUserDetailLoading(msg: String) : BlePbLoading(msg)
}
