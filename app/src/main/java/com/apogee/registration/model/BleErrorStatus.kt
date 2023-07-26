package com.apogee.registration.model

sealed class BleErrorStatus(error: String) {
    class BleImeiError(error: String) : BleErrorStatus(error)
}