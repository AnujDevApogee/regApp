package com.apogee.registration

import android.app.Application
import com.apogee.registration.instance.ApiInstance

class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        ApiInstance.getInstance()
    }
}