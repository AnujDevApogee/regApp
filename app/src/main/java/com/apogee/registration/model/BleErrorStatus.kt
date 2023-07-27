package com.apogee.registration.model

sealed class BleErrorStatus(val error: String?,val e:Throwable?) {
    class BleImeiError(error: String?, e: Throwable?) : BleErrorStatus(error, e)
    class BleSetUpConnectionError(error: String?, e: Throwable?) : BleErrorStatus(error, e)
    class BleConnectError(error: String?, e: Throwable?) : BleErrorStatus(error, e)
}