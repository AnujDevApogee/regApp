package com.apogee.registration.repository

import android.app.Application
import android.util.Log
import com.apogee.registration.instance.RoomDataBaseInstance
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest


class ModuleRegistrationDetail(application: Application) {


    private val dbResponse by lazy {
        RoomDataBaseInstance.getInstance(application)
    }


    fun getModuleDataResponse() = channelFlow {
        val moduleName = mutableListOf<String>()
        val moduleNo = mutableListOf<String>()

        dbResponse.ModelDao().selectDataBase().collectLatest {
            Log.i("DATA_BASE_REPO", "getModuleDataResponse: $it")
            it.forEach { model ->
                moduleName.add(model.device_name)
                moduleNo.add(model.device_no)
            }
            send(Pair(moduleName.toList(),moduleNo.toList()))
        }
    }


}