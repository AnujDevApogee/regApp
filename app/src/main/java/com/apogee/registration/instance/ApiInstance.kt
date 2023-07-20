package com.apogee.registration.instance

import com.apogee.apilibrary.ApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ApiInstance {


    companion object {
        private var INSTANCE: ApiInstance? = null
        fun getInstance(): ApiInstance {
            if (INSTANCE == null) {
                INSTANCE = ApiInstance()
            }
            return INSTANCE!!
        }
    }

    private val cor = CoroutineScope(SupervisorJob())

    fun coroutineJob(): CoroutineScope {
        return cor
    }

    fun getApiInstance(): ApiCall {
        return ApiCall()
    }

}