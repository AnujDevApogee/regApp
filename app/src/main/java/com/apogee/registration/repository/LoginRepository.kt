package com.apogee.registration.repository

import android.content.Context
import com.apogee.apilibrary.Interfaces.CustomCallback
import com.apogee.registration.datastore.RegistrationAppSharedPref
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.model.LoginRequest
import com.apogee.registration.model.LoginResponse
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.deserializeFromJson
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class LoginRepository(context: Context) : CustomCallback {


    private val loginSharePref by lazy {
        RegistrationAppSharedPref.getInstance(context)
    }

    private val _loginResponse =
        MutableStateFlow<DataResponse<out Any?>>(DataResponse.Loading(null))
    val loginResponse: StateFlow<DataResponse<out Any?>>
        get() = _loginResponse

    private val api by lazy {
        ApiInstance.getInstance().getApiInstance()
    }

    private val coroutine by lazy {
        ApiInstance.getInstance().coroutineJob()
    }


    fun validateUser(loginRequest: LoginRequest) {
        coroutine.launch {
            try {
                _loginResponse.value = DataResponse.Loading("Please Wait")
                delay(20000)
                api.postDataWithBody(
                    loginRequest.setJsonObject(),
                    this@LoginRepository,
                    ApiUrl.loginUrl.first,
                    ApiUrl.loginUrl.second
                )
            } catch (e: Exception) {
                _loginResponse.value = DataResponse.Error(null, e)
            }
        }
    }


    fun cancel() {
        coroutine.cancel("Cancel Login Job")
    }

    override fun onResponse(p0: Call<*>?, response: Response<*>?, p2: Int) {
        coroutine.launch {
            response?.let {
                if (it.isSuccessful) {
                    val requestBody = response.body() as ResponseBody?
                    if (requestBody != null) {

                        try {
                            val loginResponse =
                                deserializeFromJson<LoginResponse>(requestBody.string())
                         //   loginSharePref.saveLoginResponse(loginResponse!!)
                            _loginResponse.value = DataResponse.Success(
                                loginResponse
                            )
                        } catch (e: Exception) {
                            _loginResponse.value = DataResponse.Error(null, e)
                        }

                    } else {
                        _loginResponse.value =
                            DataResponse.Error("Cannot Process the Response", null)
                    }

                } else {
                    _loginResponse.value = DataResponse.Error(it.toString(), null)
                }
            } ?: run {
                _loginResponse.value = DataResponse.Error("oops something went wrong", null)
            }
        }
    }

    override fun onFailure(p0: Call<*>?, p1: Throwable?, p2: Int) {
        coroutine.launch {
            _loginResponse.value = DataResponse.Error(p0.toString(), p1)
        }
    }

}