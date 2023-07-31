package com.apogee.registration.model.pb

sealed class BlePbError(val error: String?, val e: Throwable?) {
    class ConnectionAndImeiError(error: String?, e: Throwable?) : BlePbError(error, e)
    class DeviceRegAndSubError(error: String?, e: Throwable?) : BlePbError(error, e)
    class DeviceRegBleAndDeviceConfirmError(error: String?, e: Throwable?) : BlePbError(error, e)
    class BLESubBLEAndDeviceSubBleApiError(error: String?, e: Throwable?) : BlePbError(error, e)
    class ValidateUserDetailError(error: String?, e: Throwable?) : BlePbError(error, e)
    class BLERenamingAndApIError(error: String?, e: Throwable?) : BlePbError(error, e)
}