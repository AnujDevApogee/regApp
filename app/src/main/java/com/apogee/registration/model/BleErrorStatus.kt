package com.apogee.registration.model

sealed class BleErrorStatus(error: String,e:Throwable?) {
    class BleImeiError(error: String,e:Throwable?) : BleErrorStatus(error,e)
}