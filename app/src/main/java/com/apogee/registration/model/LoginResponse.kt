package com.apogee.registration.model


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("Data")
    val data: List<Data>?
)