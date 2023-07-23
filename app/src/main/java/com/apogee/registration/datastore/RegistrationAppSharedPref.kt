package com.apogee.registration.datastore

import android.annotation.SuppressLint
import android.content.Context
import com.apogee.registration.model.LoginResponse
import com.apogee.registration.utils.deserializeFromJson
import com.apogee.registration.utils.serializeToJson

class RegistrationAppSharedPref(private val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: RegistrationAppSharedPref? = null

        fun getInstance(context: Context): RegistrationAppSharedPref {
            if (INSTANCE == null) {
                INSTANCE = RegistrationAppSharedPref(context)
            }
            return INSTANCE!!
        }
    }

    private val loginXML = "LOGIN_DETAIL"
    private val email = "Email"
    private val pass = "Password"
    private val remember = "Password"

    private val loginResponse = "Login_Response"
    private val sharedLoginPref by lazy {
        context.getSharedPreferences(loginXML, Context.MODE_PRIVATE)
    }

    fun saveUserNameAndPassword(email: String, pass: String, remember: Boolean) {
        val edit = sharedLoginPref.edit()
        edit.putString(this.email, email)
        edit.putString(this.pass, pass)
        edit.putBoolean(this.remember, remember)
        edit.apply()
    }


    fun saveLoginResponse(loginResponse: LoginResponse) {
        val edit = sharedLoginPref.edit()
        edit.putString(this.loginResponse, serializeToJson(loginResponse))
        edit.apply()
    }


    fun getLoginCredential(): Pair<Pair<String, String>, Boolean> {
        return Pair(
            Pair(
                sharedLoginPref.getString(email, "") ?: "",
                sharedLoginPref.getString(pass, "") ?: ""
            ), sharedLoginPref.getBoolean(remember, false)
        )
    }

    fun getLoginResponse(): LoginResponse? {
        return deserializeFromJson<LoginResponse>(sharedLoginPref.getString(loginResponse, "{}"))
    }

}