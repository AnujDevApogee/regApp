package com.apogee.registration.repository

import com.apogee.apilibrary.Interfaces.CustomCallback
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.checkVaildString
import com.apogee.registration.utils.createLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class BleStatusCheckRepository : CustomCallback {


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


    private val searchString="Registration, Ble Renaming and Subscription Successfull"

    private var deviceName:Pair<String,String>?=null

    fun sendBleStatusResult(req: String) {
        coroutine.launch {
            try {
                _data.value = DataResponse.Loading("Please Wait Renaming Ble Device..")
                deviceName=req.split(",".toRegex()).let {
                    Pair(it[5],it[2])//Device Name, Device Reg No
                }
                createLog("CREATE_LS","Device Name $deviceName and $req for url ${ApiUrl.bleStatusCheck}")
                api.postDataWithContentType(
                    req.trim(),
                    this@BleStatusCheckRepository,
                    ApiUrl.bleStatusCheck.first,
                    ApiUrl.bleStatusCheck.second,
                    "application/json"
                )
            }catch (e:Exception){
                _data.value=DataResponse.Error("Cannot send request",e)
            }
        }
    }


    override fun onResponse(p0: Call<*>?, response: Response<*>?, p2: Int) {
        coroutine.launch {
            response?.let {
                if (it.isSuccessful) {
                    val requestBody = response.body() as ResponseBody?
                    if (requestBody != null) {

                        try {
                            val deviceRegResponse = requestBody.string()
                            createLog("CREATE_LS","Response Body $deviceRegResponse")
                            if (checkVaildString(deviceRegResponse)) {
                                _data.value =
                                    DataResponse.Error("Cannot Connection", null)
                            } else {
                                _data.value =
                                    if (deviceRegResponse.contains(searchString, true) && deviceName!=null) {
                                        DataResponse.Success(deviceName!!)
                                    }else if(deviceName==null){
                                        DataResponse.Error("Lost some data",null)
                                    }else {
                                        DataResponse.Error(deviceRegResponse, null)
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