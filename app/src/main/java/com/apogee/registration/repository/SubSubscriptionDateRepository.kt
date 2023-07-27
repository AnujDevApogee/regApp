package com.apogee.registration.repository

import com.apogee.apilibrary.Interfaces.CustomCallback
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.checkVaildString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class SubSubscriptionDateRepository : CustomCallback {


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


    private var deviceRegistrationString: String? = null

    fun sendSubSubscriptionRequest(req: Pair<String, String>) {
        coroutine.launch {
            _data.value = DataResponse.Loading("Please Wait Adding Subscription Time...")
            deviceRegistrationString = req.second
            api.postDataWithContentType(
                req.first,
                this@SubSubscriptionDateRepository,
                ApiUrl.deviceSubscriptionUrl.first,
                ApiUrl.deviceSubscriptionUrl.second,
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
                            val subscriptionResponse = requestBody.string()
                            if (checkVaildString(subscriptionResponse)) {
                                _data.value =
                                    DataResponse.Error("Cannot Connection", null)
                            } else {
                                _data.value =
                                    if (subscriptionResponse.contains(
                                            "Data Saved",
                                            true
                                        ) && deviceRegistrationString != null
                                    ) {
                                        DataResponse.Success(deviceRegistrationString!!)
                                    } else if (deviceRegistrationString == null) {
                                        DataResponse.Error("Cannot find the Device Protocol", null)
                                    } else {
                                        DataResponse.Error(subscriptionResponse, null)
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