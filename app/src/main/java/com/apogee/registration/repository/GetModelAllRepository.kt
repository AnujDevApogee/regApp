package com.apogee.registration.repository

import android.app.Application
import androidx.room.withTransaction
import com.apogee.apilibrary.Interfaces.CustomCallback
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.instance.RoomDataBaseInstance
import com.apogee.registration.model.getmodel.ModuleDataList
import com.apogee.registration.utils.ApiUrl
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.deserializeFromJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class GetModelAllRepository(application: Application) : CustomCallback {


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

    private val dbInstance by lazy {
        RoomDataBaseInstance.getInstance(application)
    }


    fun sendFetchingDeviceInfo() {
        coroutine.launch {
            try {
                _data.value = DataResponse.Loading("Please Wait Fetching Device Info")
                api.postDataWithoutBody(
                    this@GetModelAllRepository,
                    ApiUrl.moduleApp.first,
                    ApiUrl.moduleApp.second,
                )
            } catch (e: Exception) {
                _data.value = DataResponse.Error("Cannot send request", e)
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
                            val modelList = deserializeFromJson<ModuleDataList>(deviceRegResponse)
                            _data.value=modelList?.let { ls ->
                                dbInstance.withTransaction {
                                    dbInstance.ModelDao().deleteAll()
                                    dbInstance.ModelDao().insertData(modelList.models)
                                    DataResponse.Success("Module Added Successfully")
                                }
                            } ?: DataResponse.Error("Cannot Find Module List", null)
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