package com.apogee.registration.repository

import com.apogee.apilibrary.Interfaces.CustomCallback
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.model.LoginResponse
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.deserializeFromJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class SendImeiNumberRepository : CustomCallback {


    private val _data =
        MutableStateFlow<DataResponse<out Any?>?>(null)
    val data: StateFlow<DataResponse<out Any?>?>
        get() = _data


    private val api by lazy {
        ApiInstance.getInstance().getApiInstance()
    }

    private val coroutine by lazy {
        ApiInstance.getInstance().coroutineJob()
    }


    fun sendImeiNumber(req: String) {
        coroutine.launch {
            _data.value = DataResponse.Loading("Please Wail Sending Imei")
            api.postDataWithContentType(
                req,
                this@SendImeiNumberRepository,
                ApiUrl.loginUrl.first,
                ApiUrl.loginUrl.second,
                "application/json"
            )
        }
    }


    override fun onResponse(p0: Call<*>?, response: Response<*>?, p2: Int) {
        coroutine.launch {
            response?.let {
                if (it.isSuccessful) {
                    val requestBody = response.body() as ResponseBody?
                    if (requestBody != null) {

                        try {
                            val responseImei =
                                deserializeFromJson<LoginResponse>(requestBody.string())
                            if (responseImei == null || responseImei.data.isNullOrEmpty()) {
                                _data.value =
                                    DataResponse.Error("Cannot Connection", null)
                            } else {
                                _data.value = DataResponse.Success(
                                    responseImei
                                )
                            }
                        } catch (e: Exception) {
                            _data.value = DataResponse.Error(null, e)
                        }

                    } else {
                        _data.value =
                            DataResponse.Error("Cannot Process the Response", null)
                    }

                } else {
                    _data.value = DataResponse.Error(it.toString(), null)
                }
            } ?: run {
                _data.value = DataResponse.Error("oops something went wrong", null)
            }
        }
    }

    override fun onFailure(p0: Call<*>?, p1: Throwable?, p2: Int) {
        coroutine.launch {
            _data.value = DataResponse.Error(p0.toString(), p1)
        }
    }
}