package com.apogee.registration.utils

sealed class DataResponse<T>(
    val data: T? = null,
    val exception: Throwable? = null
) {
    class Loading<T>(data: T?) : DataResponse<T>(data)
    class Success<T>(data: T) : DataResponse<T>(data)
    class Error<T>(data: T? = null, exception: Throwable?) : DataResponse<T>(data, exception)
}