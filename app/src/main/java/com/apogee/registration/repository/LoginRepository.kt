package com.apogee.registration.repository

import com.apogee.apilibrary.Interfaces.CustomCallback
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class LoginRepository : CustomCallback {


    private val _loginResponse = MutableStateFlow<DataResponse<out Any?>>(DataResponse.Loading("Please wait.."))
    val loginResponse: StateFlow<DataResponse<out Any?>>
        get() = _loginResponse

    private val api by lazy {
        ApiInstance.getInstance().getApiInstance()
    }

    private val coroutine by lazy {
        ApiInstance.getInstance().coroutineJob()
    }


    fun validateUser() {
        coroutine.launch {
            _loginResponse.value = DataResponse.Loading("Please wait..")
            api.postDataWithBody(
                "",
                this@LoginRepository,
                ApiUrl.loginUrl.first,
                ApiUrl.loginUrl.second
            )
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
                            val responseString = requestBody.string()
                            _loginResponse.value = DataResponse.Success(responseString)
                        } catch (e: Exception) {
                            _loginResponse.value = DataResponse.Error(null, e)
                        }

                    } else {
                        null
                    }

                } else {
                    _loginResponse.value = DataResponse.Error(it.toString(), null)
                }
                ""
            } ?: {
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