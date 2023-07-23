package com.apogee.registration.model


import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName


data class LoginRequest(
    @SerializedName("password")
    val password: String,
    @SerializedName("project_name")
    val projectName: String,
    @SerializedName("username")
    val username: String
){
    fun setJsonObject(): JsonObject {
        val json=JsonObject()
        json.add("password",Gson().toJsonTree(this.password))
        json.add("project_name",Gson().toJsonTree(this.projectName))
        json.add("username",Gson().toJsonTree(this.username))
        return json
    }
}