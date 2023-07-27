package com.apogee.registration.repository

import com.apogee.apilibrary.Interfaces.CustomCallback
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.model.DeviceRegModel
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.checkVaildString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class DeviceRegRepository : CustomCallback {


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


    private var req: DeviceRegModel? = null

    fun sendRequest(req: DeviceRegModel) {
        coroutine.launch {
            _data.value = DataResponse.Loading("Please Wait Registration Device")
            this@DeviceRegRepository.req = req
            api.postDataWithContentType(
                this@DeviceRegRepository.req!!.deviceRegRequestBody,
                this@DeviceRegRepository,
                ApiUrl.deviceRegUrl.first,
                ApiUrl.deviceRegUrl.second,
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
                            val responseImei = requestBody.string()
                            if (checkVaildString(responseImei)) {
                                _data.value =
                                    DataResponse.Error("Cannot Connection", null)
                            } else {
                                _data.value =
                                    if (responseImei.startsWith("$$$$") && responseImei.endsWith("####") && req != null) {
                                        DataResponse.Success(Pair(req!!, responseImei))
                                    } else if (req == null) {
                                        DataResponse.Error("Oops Something Went Wrong", null)
                                    } else {
                                        DataResponse.Error(responseImei, null)
                                    }
                            }
                        } catch (e: Exception) {
                            _data.value = DataResponse.Error(null, e)
                        }

                    } else {
                        _data.value =
                            DataResponse.Error(
                                "Cannot Process the Device Registration Response",
                                null
                            )
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