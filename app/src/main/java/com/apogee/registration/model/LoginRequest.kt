package com.apogee.registration.model


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("password")
    val password: String,
    @SerializedName("project_name")
    val projectName: String,
    @SerializedName("username")
    val username: String
)